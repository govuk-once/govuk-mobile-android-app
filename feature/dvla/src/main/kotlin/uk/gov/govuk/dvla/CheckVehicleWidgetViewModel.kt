package uk.gov.govuk.dvla

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import uk.gov.govuk.analytics.AnalyticsClient
import javax.inject.Inject

@HiltViewModel
internal class CheckVehicleWidgetViewModel @Inject constructor(
    private val analyticsClient: AnalyticsClient
) : ViewModel() {

    companion object {
        private const val SECTION = "Driving"
    }

    fun onSearchClicked(text: String) {
        analyticsClient.buttonClick(
            text = text,
            section = SECTION
        )
    }
}
