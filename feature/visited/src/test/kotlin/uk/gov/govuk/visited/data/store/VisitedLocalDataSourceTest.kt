package uk.gov.govuk.visited.data.store

import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.visited.data.model.VisitedItemEntity

class VisitedLocalDataSourceTest {

    private val dao = mockk<VisitedDao>(relaxed = true)
    private lateinit var dataSource: VisitedLocalDataSource

    @Before
    fun setup() {
        dataSource = VisitedLocalDataSource(dao)
    }

    @Test
    fun `Given visited items in db, when get visited items, then emit all visited items`() {
        val items = listOf(
            VisitedItemEntity(title = "title1", url = "url1", lastVisited = 2),
            VisitedItemEntity(title = "title2", url = "url2", lastVisited = 1)
        )
        every { dao.getVisitedItems() } returns flowOf(items)

        runTest {
            val result = dataSource.visitedItems.first()
            assertEquals(2, result.size)
            assertEquals("title1", result[0].title)
            assertEquals("title2", result[1].title)
        }
    }

    @Test
    fun `Given a visited item, when visited, then upsert into db`() {
        runTest {
            dataSource.insertOrUpdate("title1", "url1")

            coVerify { dao.upsert(match { it.title == "title1" && it.url == "url1" }) }
        }
    }

    @Test
    fun `Given a visited item, when removed, then delete from db`() {
        runTest {
            dataSource.remove("title1", "url1")
            coVerify { dao.delete("title1", "url1") }
        }
    }

    @Test
    fun `Given visited items, when cleared, then delete all from db`() {
        runTest {
            dataSource.clear()
            coVerify { dao.deleteAll() }
        }
    }
}
