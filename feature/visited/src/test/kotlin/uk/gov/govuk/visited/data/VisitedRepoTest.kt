package uk.gov.govuk.visited.data

import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import uk.gov.govuk.visited.data.model.VisitedItem
import uk.gov.govuk.visited.data.store.VisitedLocalDataSource
import java.time.LocalDate

class VisitedRepoTest {
    private val localDataSource = mockk<VisitedLocalDataSource>(relaxed = true)

    @Test
    fun `Given the local data source is empty, when get visited items, then emit nothing`() {
        every { localDataSource.visitedItems } returns flowOf(emptyList())

        val repo = VisitedRepo(localDataSource)

        runTest {
            assertEquals(0, repo.visitedItems.first().size)
        }
    }

    @Test
    fun `Given the local data source is not empty, when get visited items, then emit visited items`() {
        val lastVisitedMillis = LocalDate.now().toEpochDay()

        every { localDataSource.visitedItems } returns flowOf(
            listOf(
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
        )
        val repo = VisitedRepo(localDataSource)

        runTest {
            assertEquals(2, repo.visitedItems.first().size)
        }
    }

    @Test
    fun `Given the repo is cleared, then clear local data source`() {
        val repo = VisitedRepo(localDataSource)

        runTest {
            repo.clear()
        }

        coVerify {
            localDataSource.clear()
        }
    }
}
