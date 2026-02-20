package uk.gov.govuk.topics.data.local

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.topics.data.local.model.LocalTopicItem
import uk.gov.govuk.topics.data.remote.model.RemoteTopicItem

class TopicsLocalDataSourceTest {

    private val realmProvider = mockk<TopicsRealmProvider>(relaxed = true)
    private val dataStore = mockk<TopicsDataStore>(relaxed = true)

    private lateinit var realm: Realm

    private lateinit var localDataSource: TopicsLocalDataSource

    @Before
    fun setup() {
        val config = RealmConfiguration.Builder(schema = setOf(LocalTopicItem::class))
            .inMemory() // In-memory Realm for testing
            .build()

        // Open the Realm instance
        realm = Realm.open(config)

        coEvery { realmProvider.open() } returns realm

        localDataSource = TopicsLocalDataSource(realmProvider, dataStore)
    }

    @After
    fun tearDown() {
        realm.close()
    }

    @Test
    fun `Given topic items in realm, when get topics, then emit selected topic items`() {
        val expected = listOf(
            LocalTopicItem().apply {
                ref = "ref1"
                title = "title1"
                description = "description1"
                isSelected = true
            },
            LocalTopicItem().apply {
                ref = "ref2"
                title = "title2"
                description = "description2"
                isSelected = false
            }
        )

        runTest {
            realm.write {
                copyToRealm(expected[0])
                copyToRealm(expected[1])
            }

            val topics = localDataSource.topics.first()

            assertEquals(2, topics.size)
            assertEquals("ref1", topics[0].ref)
            assertTrue(topics[0].isSelected)
            assertEquals("ref2", topics[1].ref)
            assertFalse(topics[1].isSelected)
        }
    }

    @Test
    fun `Given new remote topics, when topics are synced, then insert new topics as not selected`() {
        val remoteTopics = listOf(
            RemoteTopicItem(
                ref = "ref",
                title = "title",
                description = "description"
            )
        )

        runTest {
            localDataSource.sync(remoteTopics)

            val topics = realm.query<LocalTopicItem>().find()
            assertEquals(1, topics.size)
            assertEquals("ref", topics[0].ref)
            assertFalse(topics[0].isSelected)
        }
    }

    @Test
    fun `Given a topic is not present in the remote topics, when topics are synced, then delete the topic`() {
        runTest {
            realm.write {
                copyToRealm(
                    LocalTopicItem().apply {
                        ref = "ref1"
                        isSelected = false
                    }
                )
            }

            localDataSource.sync(emptyList())

            val topics = realm.query<LocalTopicItem>().find()
            assertTrue(topics.isEmpty())
        }
    }

    @Test
    fun `Given a topic is updated and topics have not been customised, when topics are synced, then update the topic in realm as not selected`() {
        val remoteTopics = listOf(
            RemoteTopicItem(
                ref = "ref1",
                title = "title2",
                description = "desc2"
            )
        )

        coEvery { dataStore.isTopicsCustomised() } returns false

        runTest {
            realm.write {
                copyToRealm(
                    LocalTopicItem().apply {
                        ref = "ref1"
                        title = "title1"
                        description = "desc1"
                        isSelected = true
                    }
                )
            }

            localDataSource.sync(remoteTopics)

            val topics = realm.query<LocalTopicItem>().find()
            assertEquals(1, topics.size)
            assertEquals("ref1", topics[0].ref)
            assertEquals("title2", topics[0].title)
            assertEquals("desc2", topics[0].description)
            assertFalse(topics[0].isSelected)
        }
    }

    @Test
    fun `Given a topic is updated and topics have been customised, when topics are synced, then update the topic in realm and maintain selected state`() {
        val remoteTopics = listOf(
            RemoteTopicItem(
                ref = "ref1",
                title = "title2",
                description = "desc2"
            )
        )

        coEvery { dataStore.isTopicsCustomised() } returns true

        runTest {
            realm.write {
                copyToRealm(
                    LocalTopicItem().apply {
                        ref = "ref1"
                        title = "title1"
                        description = "desc1"
                        isSelected = true
                    }
                )
            }

            localDataSource.sync(remoteTopics)

            val topics = realm.query<LocalTopicItem>().find()
            assertEquals(1, topics.size)
            assertEquals("ref1", topics[0].ref)
            assertEquals("title2", topics[0].title)
            assertEquals("desc2", topics[0].description)
            assertTrue(topics[0].isSelected)
        }
    }

    @Test
    fun `Given a topic is selected, then update the topic in realm`() {
        runTest {
            realm.write {
                copyToRealm(
                    LocalTopicItem().apply {
                        ref = "ref1"
                        isSelected = false
                    }
                )
            }

            localDataSource.toggleSelection("ref1", true)

            val topics = realm.query<LocalTopicItem>().find()
            assertEquals(1, topics.size)
            assertEquals("ref1", topics[0].ref)
            assertTrue(topics[0].isSelected)
        }
    }

    @Test
    fun `Given a topic is deselected, then update the topic in realm`() {
        runTest {
            realm.write {
                copyToRealm(
                    LocalTopicItem().apply {
                        ref = "ref1"
                        isSelected = true
                    }
                )
            }

            localDataSource.toggleSelection("ref1", false)

            val topics = realm.query<LocalTopicItem>().find()
            assertEquals(1, topics.size)
            assertEquals("ref1", topics[0].ref)
            assertFalse(topics[0].isSelected)
        }
    }

    @Test
    fun `Given multiple topics are selected, then update the topics in realm`() {
        runTest {
            realm.write {
                copyToRealm(
                    LocalTopicItem().apply {
                        ref = "ref1"
                        isSelected = false
                    }
                )
                copyToRealm(
                    LocalTopicItem().apply {
                        ref = "ref2"
                        isSelected = false
                    }
                )
            }

            localDataSource.selectAll(listOf("ref1", "ref2"))

            val topics = realm.query<LocalTopicItem>().find()
            assertEquals(2, topics.size)
            assertEquals("ref1", topics[0].ref)
            assertTrue(topics[0].isSelected)
            assertEquals("ref2", topics[1].ref)
            assertTrue(topics[1].isSelected)
        }
    }

    @Test
    fun `Given topics are customised, when is topics customised, then return true`() {
        runTest {
            coEvery { dataStore.isTopicsCustomised() } returns true

            assertTrue(localDataSource.isTopicsCustomised())
        }
    }

    @Test
    fun `Given topics are not customised, when is topics customised, then return false`() {
        runTest {
            coEvery { dataStore.isTopicsCustomised() } returns false

            assertFalse(localDataSource.isTopicsCustomised())
        }
    }

    @Test
    fun `Given a user customises topics, when topics customised, then update data store`() {
        runTest {
            localDataSource.topicsCustomised()

            coVerify {
                dataStore.topicsCustomised()
            }
        }
    }

    @Test
    fun `Given topics, when has topics, then return true`() {
        runTest {
            realm.write {
                copyToRealm(
                    LocalTopicItem().apply {
                        ref = "ref1"
                        title = "title1"
                        description = "description1"
                        isSelected = true
                    }
                )
            }

            assertTrue(localDataSource.hasTopics())
        }
    }

    @Test
    fun `Given no topics, when has topics, then return false`() {
        runTest {
            assertFalse(localDataSource.hasTopics())
        }
    }

    @Test
    fun `Given topics, when clear, then select all in realm and clear data store`() {
        runTest {
            realm.write {
                copyToRealm(
                    LocalTopicItem().apply {
                        ref = "ref1"
                        title = "title1"
                        description = "description1"
                        isSelected = false
                    }
                )

                copyToRealm(
                    LocalTopicItem().apply {
                        ref = "ref2"
                        title = "title2"
                        description = "description2"
                        isSelected = false
                    }
                )

                assertTrue(query<LocalTopicItem>().find().isNotEmpty())
            }

            localDataSource.clear()

            val topics = realm.query<LocalTopicItem>().find().toList()

            for (topic in topics) {
                assertFalse(topic.isSelected)
            }

            coVerify { dataStore.clear() }
        }
    }
}