package uk.govuk.app.local

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import uk.gov.govuk.analytics.AnalyticsClient
import javax.inject.Inject

@HiltViewModel
internal class LocalExplainerViewModel @Inject constructor(
    private val analyticsClient: AnalyticsClient
): ViewModel() {

    companion object {
        private const val SCREEN_CLASS = "LocalExplainerScreen"
        private const val SCREEN_NAME = "Local Explainer"
        private const val TITLE = "Local Explainer"
    }

    fun onPageView() {
        analyticsClient.screenView(
            screenClass = SCREEN_CLASS,
            screenName = SCREEN_NAME,
            title = TITLE
        )
    }

    fun onButtonClick(text: String) {
        analyticsClient.buttonClick(
            text = text,
            section = SECTION
        )
    }
}