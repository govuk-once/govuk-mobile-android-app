package uk.gov.govuk.home

import io.mockk.mockk
import io.mockk.verify
import org.junit.Test
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.config.data.ConfigRepo

class HomeViewModelTest {

    private val analyticsClient = mockk<AnalyticsClient>(relaxed = true)

    @Test
    fun `Given a page view, and the user has opted into chat, then log analytics`() {
        val viewModel = HomeViewModel(analyticsClient)

        viewModel.onPageView()

        verify {
            analyticsClient.screenView(
                screenClass = "HomeScreen",
                screenName = "Homepage",
                title = "Homepage"
            )
        }
    }

    @Test
    fun `Given a page view, and the user has opted out of chat, then log analytics`() {
        val viewModel = HomeViewModel(analyticsClient)

        viewModel.onPageView()

        verify {
            analyticsClient.screenView(
                screenClass = "HomeScreen",
                screenName = "Homepage",
                title = "Homepage"
            )
        }
    }
}
