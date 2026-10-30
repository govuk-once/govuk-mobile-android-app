package uk.gov.govuk.search.data.local

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.search.data.local.model.LocalSearchItemEntity

class SearchLocalDataSourceTest {

    private val dao = mockk<SearchDao>(relaxed = true)
    private lateinit var dataSource: SearchLocalDataSource

    @Before
    fun setup() {
        dataSource = SearchLocalDataSource(dao)
    }

    @Test
    fun `Given previous searches in db, when collect previous searches, then return previous searches`() {
        val items = listOf(
            LocalSearchItemEntity("cat", 1),
            LocalSearchItemEntity("dog", 0)
        )
        every { dao.getPreviousSearches() } returns flowOf(items)

        runTest {
            val result = dataSource.previousSearches.first()
            assertEquals(2, result.size)
            assertEquals("cat", result[0].searchTerm)
            assertEquals("dog", result[1].searchTerm)
        }
    }

    @Test
    fun `Given there are less than 5 previous searches, when a user performs a new search, then insert into db`() {
        coEvery { dao.getAllSortedByTimestamp() } returns listOf(
            LocalSearchItemEntity("fox", 5),
            LocalSearchItemEntity("badger", 3),
            LocalSearchItemEntity("pig", 2),
            LocalSearchItemEntity("cat", 1),
            LocalSearchItemEntity("dog", 0)
        )

        runTest {
            dataSource.insertOrUpdatePreviousSearch("fox")

            coVerify { dao.insertOrUpdate(any()) }
            coVerify(exactly = 0) { dao.delete(any()) }
        }
    }

    @Test
    fun `Given there are 5 or more previous searches, when a user performs a new search, then insert into db and remove the oldest search`() {
        coEvery { dao.getAllSortedByTimestamp() } returns listOf(
            LocalSearchItemEntity("duck", 5),
            LocalSearchItemEntity("fox", 4),
            LocalSearchItemEntity("badger", 3),
            LocalSearchItemEntity("pig", 2),
            LocalSearchItemEntity("cat", 1),
            LocalSearchItemEntity("dog", 0)
        )

        runTest {
            dataSource.insertOrUpdatePreviousSearch("duck")

            coVerify { dao.insertOrUpdate(any()) }
            coVerify { dao.delete("dog") }
        }
    }

    @Test
    fun `Given there is an existing previous search, when a user performs the same search again, then update the search timestamp`() {
        coEvery { dao.getAllSortedByTimestamp() } returns listOf(
            LocalSearchItemEntity("dog", 999)
        )

        runTest {
            dataSource.insertOrUpdatePreviousSearch("dog")

            coVerify { dao.insertOrUpdate(match { it.searchTerm == "dog" && it.timestamp > 0 }) }
        }
    }

    @Test
    fun `Given a user removes a previous search, then delete from db`() {
        runTest {
            dataSource.removePreviousSearch("cat")
            coVerify { dao.delete("cat") }
        }
    }

    @Test
    fun `Given a user removes all previous searches, then delete all from db`() {
        runTest {
            dataSource.removeAllPreviousSearches()
            coVerify { dao.deleteAll() }
        }
    }

    @Test
    fun `Given the data source is cleared, then delete all from db`() {
        runTest {
            dataSource.clear()
            coVerify { dao.deleteAll() }
        }
    }
}
