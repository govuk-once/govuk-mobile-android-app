package uk.gov.govuk.visited.data.store

import io.mockk.coEvery
import io.mockk.mockk
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.visited.data.model.VisitedItem
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset

class VisitedLocalDataSourceTest {
    private val realmProvider = mockk<VisitedRealmProvider>(relaxed = true)

    private lateinit var realm: Realm

    @Before
    fun setup() {
        val config = RealmConfiguration.Builder(schema = setOf(VisitedItem::class))
            .inMemory() // In-memory Realm for testing
            .build()

        // Open the Realm instance
        realm = Realm.open(config)

        coEvery { realmProvider.open() } returns realm
    }

    @After
    fun tearDown() {
        realm.close()
    }

    @Test
    fun `Given visited items in realm, when get visited items, then emit all visited items`() {
        var lastVisitedMillis = LocalDate.now().toEpochDay()

        val expected = listOf(
            VisitedItem().apply {
                title = "title1"
                url = "url1"
                lastVisited = lastVisitedMillis
            },
            VisitedItem().apply {
                title = "title2"
                url = "url2"
                lastVisited = lastVisitedMillis
            }
        )

        runTest {
            realm.write {
                copyToRealm(expected[0])
                copyToRealm(expected[1])
            }

            val localDataSource = VisitedLocalDataSource(realmProvider)

            val visitedItems = localDataSource.visitedItems.first()

            assertEquals(2, visitedItems.size)

            assertEquals("title1", visitedItems[0].title)
            assertEquals("url1", visitedItems[0].url)
            assertEquals(lastVisitedMillis, visitedItems[0].lastVisited)

            assertEquals("title2", visitedItems[1].title)
            assertEquals("url2", visitedItems[1].url)
            assertEquals(lastVisitedMillis, visitedItems[1].lastVisited)
        }
    }

    @Test
    fun `Given no historical visited items, when visited, then create the visited item`() {
        val today = LocalDate.now()

        runTest {
            val localDataSource = VisitedLocalDataSource(realmProvider)

            localDataSource.insertOrUpdate("title1", "url1")

            val visitedItems = localDataSource.visitedItems.first()
            val expectedEpochDay =
                LocalDateTime.ofEpochSecond(visitedItems[0].lastVisited, 0, ZoneOffset.UTC)
                    .toLocalDate().toEpochDay()

            assertEquals(today.toEpochDay(), expectedEpochDay)
        }
    }

    @Test
    fun `Given two historical visited items, when one is re-visited, then update the correct lastVisited date`() {
        val today = LocalDateTime.now()
        val fiveDaysAgo = today.minusDays(5)

        runTest {
            val localDataSource = VisitedLocalDataSource(realmProvider)

            localDataSource.insertOrUpdate("title1", "url1", fiveDaysAgo)
            localDataSource.insertOrUpdate("title2", "url2", fiveDaysAgo)

            var visitedItems = localDataSource.visitedItems.first()

            assertEquals(fiveDaysAgo.toEpochSecond(ZoneOffset.UTC), visitedItems[0].lastVisited)
            assertEquals(fiveDaysAgo.toEpochSecond(ZoneOffset.UTC), visitedItems[1].lastVisited)

            localDataSource.insertOrUpdate("title2", "url2", today)

            // Re-read the visited items from the local data source
            visitedItems = localDataSource.visitedItems.first()

            // Don't rely on the ordering, check for the correct title
            visitedItems.forEach { visitedItem ->
                if (visitedItem.title == "title2") {
                    assertEquals(today.toEpochSecond(ZoneOffset.UTC), visitedItem.lastVisited)
                } else {
                    assertEquals(fiveDaysAgo.toEpochSecond(ZoneOffset.UTC), visitedItem.lastVisited)
                }
            }
        }
    }

    @Test
    fun `Given a historical visited item, when re-visited, then update the lastVisited date`() {
        val today = LocalDateTime.now()
        val fiveDaysAgo = today.minusDays(5)

        runTest {
            val localDataSource = VisitedLocalDataSource(realmProvider)

            localDataSource.insertOrUpdate("title1", "url1", fiveDaysAgo)

            var visitedItems = localDataSource.visitedItems.first()

            assertEquals(fiveDaysAgo.toEpochSecond(ZoneOffset.UTC), visitedItems[0].lastVisited)

            localDataSource.insertOrUpdate("title1", "url1", today)

            // Re-read the visited items from the local data source
            visitedItems = localDataSource.visitedItems.first()

            assertEquals(today.toEpochSecond(ZoneOffset.UTC), visitedItems[0].lastVisited)
        }
    }

    @Test
    fun `Given a visited item, when it is removed, then we have no visited items`() {
        val today = LocalDateTime.now()

        runTest {
            val localDataSource = VisitedLocalDataSource(realmProvider)

            localDataSource.insertOrUpdate("title1", "url1", today)

            localDataSource.remove("title1", "url1")

            val visitedItems = localDataSource.visitedItems.first()

            assertEquals(0, visitedItems.size)
        }
    }

    @Test
    fun `Given more than one visited item, when one is removed, then the correct visited items remain`() {
        val today = LocalDateTime.now()

        runTest {
            val localDataSource = VisitedLocalDataSource(realmProvider)

            localDataSource.insertOrUpdate("title1", "url1", today)
            localDataSource.insertOrUpdate("title2", "url2", today)
            localDataSource.insertOrUpdate("title3", "url3", today)

            localDataSource.remove("title2", "url2")

            val visitedItems = localDataSource.visitedItems.first()

            assertEquals(2, visitedItems.size)
            assertEquals("title1", visitedItems[0].title)
            assertEquals("title3", visitedItems[1].title)
        }
    }

    @Test
    fun `Given more than one visited item, when an incorrect title is asked to be removed, then the correct visited items remain`() {
        val today = LocalDateTime.now()

        runTest {
            val localDataSource = VisitedLocalDataSource(realmProvider)

            localDataSource.insertOrUpdate("title1", "url1", today)
            localDataSource.insertOrUpdate("title2", "url2", today)
            localDataSource.insertOrUpdate("title3", "url3", today)

            localDataSource.remove("title99", "url99")

            val visitedItems = localDataSource.visitedItems.first()

            assertEquals(3, visitedItems.size)
            assertEquals("title1", visitedItems[0].title)
            assertEquals("title2", visitedItems[1].title)
            assertEquals("title3", visitedItems[2].title)
        }
    }

    @Test
    fun `Given no visited items, when a title is asked to be removed, then the correct visited items remain`() {
        runTest {
            val localDataSource = VisitedLocalDataSource(realmProvider)

            localDataSource.remove("title99", "url99")

            val visitedItems = localDataSource.visitedItems.first()

            assertEquals(0, visitedItems.size)
        }
    }

    @Test
    fun `Given the data source is cleared, then delete all from realm`() {
        runTest {
            realm.write {
                copyToRealm(
                    VisitedItem().apply {
                        title = "title"
                        url = "url"
                        lastVisited = 0L
                    }
                )

                assertTrue(query<VisitedItem>().find().isNotEmpty())
            }

            val localDataSource = VisitedLocalDataSource(realmProvider)
            localDataSource.clear()

            assertTrue(realm.query<VisitedItem>().find().isEmpty())
        }
    }
}