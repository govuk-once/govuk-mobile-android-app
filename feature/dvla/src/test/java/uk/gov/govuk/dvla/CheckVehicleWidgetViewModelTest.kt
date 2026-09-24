package uk.gov.govuk.dvla

import io.mockk.mockk
import io.mockk.verify
import org.junit.Test
import uk.gov.govuk.analytics.AnalyticsClient

class CheckVehicleWidgetViewModelTest {

    private val analyticsClient = mockk<AnalyticsClient>(relaxed = true)

    private val viewModel = CheckVehicleWidgetViewModel(
        analyticsClient = analyticsClient
    )

    @Test
    fun `Given a search click, when onSearchClicked is called, then track button click event`() {
        val expectedText = "Check vehicle details"

        viewModel.onSearchClicked(expectedText)

        verify(exactly = 1) {
            analyticsClient.buttonClick(
                text = expectedText,
                section = "Driving"
            )
        }
    }
}