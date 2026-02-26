package uk.govuk.app.local

import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.analytics.AnalyticsClient

class LocalExplainerViewModelTest {
    private val analyticsClient = mockk<AnalyticsClient>(relaxed = true)

    private lateinit var viewModel: LocalExplainerViewModel

    @Before
    fun setup() {
        viewModel = LocalExplainerViewModel(analyticsClient)
    }

    @Test
    fun `Given a page view, then log analytics`() {
        viewModel.onPageView()

        verify {
            analyticsClient.screenView(
                screenClass = "LocalExplainerScreen",
                screenName = "Local Explainer",
                title = "Local Explainer"
            )
        }
    }

    @Test
    fun `Given a button click, then log analytics`() {
        viewModel.onButtonClick("button text")

        verify {
            analyticsClient.buttonClick(
                text = "button text",
                section = "Local"
            )
        }
    }
}