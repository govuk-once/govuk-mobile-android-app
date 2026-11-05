package uk.gov.govuk.search.data.local

import io.mockk.coEvery
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
import uk.gov.govuk.search.data.local.model.LocalSearchItem

class SearchLocalDataSourceTest {

    private val realmProvider = mockk<SearchRealmProvider>(relaxed = true)

    private lateinit var realm: Realm

    private lateinit var localDataSource: SearchLocalDataSource

    @Before
    fun setup() {
        val config = RealmConfiguration.Builder(schema = setOf(LocalSearchItem::class))
            .inMemory() // In-memory Realm for testing
            .build()

        // Open the Realm instance
        realm = Realm.open(config)

        coEvery { realmProvider.open() } returns realm

        localDataSource = SearchLocalDataSource(realmProvider)
    }

    @After
    fun tearDown() {
        realm.close()
    }

    @Test
    fun `Given previous searches in realm, when collect previous searches, then return previous searches`() {
        val expected = listOf(
            LocalSearchItem().apply {
                searchTerm = "dog"
                timestamp = 0
            },
            LocalSearchItem().apply {
                searchTerm = "cat"
                timestamp = 1
            }
        )

        runTest {
            realm.write {
                copyToRealm(expected[0])
                copyToRealm(expected[1])
            }

            val previousSearches = localDataSource.previousSearches.first()

            assertEquals(2, previousSearches.size)
            assertEquals("cat", previousSearches[0].searchTerm)
            assertEquals("dog", previousSearches[1].searchTerm)
        }
    }

    @Test
    fun `Given there are less then 5 previous searches, when a user performs a new search, then insert into realm`() {
        val previousSearches = listOf(
            LocalSearchItem().apply {
                searchTerm = "dog"
                timestamp = 0
            },
            LocalSearchItem().apply {
                searchTerm = "cat"
                timestamp = 1
            },
            LocalSearchItem().apply {
                searchTerm = "pig"
                timestamp = 2
            },
            LocalSearchItem().apply {
                searchTerm = "badger"
                timestamp = 3
            }
        )

        runTest {
            realm.write {
                copyToRealm(previousSearches[0])
                copyToRealm(previousSearches[1])
                copyToRealm(previousSearches[2])
                copyToRealm(previousSearches[3])
            }

            localDataSource.insertOrUpdatePreviousSearch("fox")

            val results = realm.query<LocalSearchItem>().find().map { it.searchTerm }
            assertEquals(5, results.size)
            assertTrue(results.contains("dog"))
            assertTrue(results.contains("cat"))
            assertTrue(results.contains("pig"))
            assertTrue(results.contains("badger"))
            assertTrue(results.contains("fox"))
        }
    }

    // Insert > 5
    @Test
    fun `Given there are 5 or more previous searches, when a user performs a new search, then insert into realm and remove the oldest search`() {
        val previousSearches = listOf(
            LocalSearchItem().apply {
                searchTerm = "dog"
                timestamp = 0
            },
            LocalSearchItem().apply {
                searchTerm = "cat"
                timestamp = 1
            },
            LocalSearchItem().apply {
                searchTerm = "pig"
                timestamp = 2
            },
            LocalSearchItem().apply {
                searchTerm = "badger"
                timestamp = 3
            },
            LocalSearchItem().apply {
                searchTerm = "fox"
                timestamp = 4
            }
        )

        runTest {
            realm.write {
                copyToRealm(previousSearches[0])
                copyToRealm(previousSearches[1])
                copyToRealm(previousSearches[2])
                copyToRealm(previousSearches[3])
                copyToRealm(previousSearches[4])
            }

            localDataSource.insertOrUpdatePreviousSearch("duck")

            val results = realm.query<LocalSearchItem>().find().map { it.searchTerm }
            assertEquals(5, results.size)
            assertTrue(results.contains("cat"))
            assertTrue(results.contains("pig"))
            assertTrue(results.contains("badger"))
            assertTrue(results.contains("fox"))
            assertTrue(results.contains("duck"))
            assertFalse(results.contains("dog"))
        }
    }

    @Test
    fun `Given there is an existing previous search, when a user performs the same search again, then update the search timestamp`() {
        val previousSearches = listOf(
            LocalSearchItem().apply {
                searchTerm = "dog"
                timestamp = 0
            },
        )

        runTest {
            realm.write {
                copyToRealm(previousSearches[0])
            }

            localDataSource.insertOrUpdatePreviousSearch("dog")

            val results = realm.query<LocalSearchItem>().find()
            assertEquals(1, results.size)
            assertEquals("dog", results[0].searchTerm)
            assertTrue(results[0].timestamp != previousSearches[0].timestamp)
        }
    }

    @Test
    fun `Given a user removes a previous search, then delete from realm`() {
        val expected = listOf(
            LocalSearchItem().apply {
                searchTerm = "dog"
                timestamp = 0
            },
            LocalSearchItem().apply {
                searchTerm = "cat"
                timestamp = 1
            }
        )

        runTest {
            realm.write {
                copyToRealm(expected[0])
                copyToRealm(expected[1])
            }

            var previousSearches = localDataSource.previousSearches.first()
            assertEquals(2, previousSearches.size)
            assertEquals("cat", previousSearches[0].searchTerm)
            assertEquals("dog", previousSearches[1].searchTerm)

            localDataSource.removePreviousSearch("cat")

            previousSearches = localDataSource.previousSearches.first()

            assertEquals(1, previousSearches.size)
            assertEquals("dog", previousSearches[0].searchTerm)
        }
    }

    @Test
    fun `Given a user removes all previous searches, then delete all from realm`() {
        val expected = listOf(
            LocalSearchItem().apply {
                searchTerm = "dog"
                timestamp = 0
            },
            LocalSearchItem().apply {
                searchTerm = "cat"
                timestamp = 1
            }
        )

        runTest {
            realm.write {
                copyToRealm(expected[0])
                copyToRealm(expected[1])
            }

            var previousSearches = localDataSource.previousSearches.first()
            assertEquals(2, previousSearches.size)
            assertEquals("cat", previousSearches[0].searchTerm)
            assertEquals("dog", previousSearches[1].searchTerm)

            localDataSource.removeAllPreviousSearches()

            previousSearches = localDataSource.previousSearches.first()
            assertTrue(previousSearches.isEmpty())
        }
    }

    @Test
    fun `Given the data source is cleared, then delete all from realm`() {
        val expected = listOf(
            LocalSearchItem().apply {
                searchTerm = "dog"
                timestamp = 0
            },
            LocalSearchItem().apply {
                searchTerm = "cat"
                timestamp = 1
            }
        )

        runTest {
            realm.write {
                copyToRealm(expected[0])
                copyToRealm(expected[1])
            }

            var previousSearches = localDataSource.previousSearches.first()
            assertEquals(2, previousSearches.size)
            assertEquals("cat", previousSearches[0].searchTerm)
            assertEquals("dog", previousSearches[1].searchTerm)

            localDataSource.clear()

            previousSearches = localDataSource.previousSearches.first()
            assertTrue(previousSearches.isEmpty())
        }
    }
}