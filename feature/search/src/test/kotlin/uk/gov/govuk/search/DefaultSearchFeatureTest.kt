package uk.gov.govuk.search

import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test
import uk.gov.govuk.search.data.SearchRepo

class DefaultSearchFeatureTest {

    private val searchRepo = mockk<SearchRepo>(relaxed = true)

    @Test
    fun `Clear clears the repo`() {
        val searchFeature = DefaultSearchFeature(searchRepo)

        runTest {
            searchFeature.clear()

            coVerify { searchRepo.clear() }
        }
    }

}