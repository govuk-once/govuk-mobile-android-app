package uk.gov.govuk.sar

import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.analytics.AnalyticsClient

@OptIn(ExperimentalCoroutinesApi::class)
class SubjectAccessRequestViewModelTest {
    private val analyticsClient = mockk<AnalyticsClient>(relaxed = true)
    private lateinit var viewModel: SubjectAccessRequestViewModel

    @Before
    fun setup() {
        coEvery { analyticsClient.isAnalyticsEnabled() } returns true

        viewModel = SubjectAccessRequestViewModel(analyticsClient)
    }

    @Test
    fun `Given an explainer page view, then log analytics`() {
        viewModel.onExplainerPageView()

        verify {
            analyticsClient.screenView(
                screenClass = "SubjectAccessRequestExplainerScreen",
                screenName = "Subject Access Request Explainer",
                title = "Subject Access Request Explainer"
            )
        }
    }

    @Test
    fun `Given an display page view, then log analytics`() {
        viewModel.onDisplayPageView()

        verify {
            analyticsClient.screenView(
                screenClass = "SubjectAccessRequestDisplayScreen",
                screenName = "Subject Access Request Display",
                title = "Subject Access Request Display"
            )
        }
    }

    @Test
    fun `Given a button click, then log analytics`() {
        viewModel.onButtonClick("Text")

        verify {
            analyticsClient.buttonClick(
                text = "Text",
                section = "Subject Access Request"
            )
        }
    }
}
