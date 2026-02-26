package uk.gov.govuk.visited

import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test
import uk.gov.govuk.visited.data.VisitedRepo
import uk.gov.govuk.visited.data.store.VisitedLocalDataSource

class VisitedClientTest {
    private val visitedRepo = mockk<VisitedRepo>(relaxed = true)
    private val visitedLocalDataSource = mockk<VisitedLocalDataSource>(relaxed = true)

    @Test
    fun `Given the user clicks on a visitable item, then run the insertOrUpdate function`() {
        val visitedClient = VisitedClient(visitedRepo, visitedLocalDataSource)

        runTest {
            visitedClient.visitableItemClick("title", "url")

            coVerify {
                visitedLocalDataSource.insertOrUpdate("title", "url", any())
            }
        }
    }

    @Test
    fun `Given the feature is cleared, then clear repo`() {
        val visitedClient = VisitedClient(visitedRepo, visitedLocalDataSource)

        runTest {
            visitedClient.clear()

            coVerify {
                visitedRepo.clear()
            }
        }
    }
}
