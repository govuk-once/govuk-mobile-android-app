package uk.gov.govuk.sar

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import uk.gov.govuk.analytics.AnalyticsClient
import javax.inject.Inject

@HiltViewModel
internal class SubjectAccessRequestViewModel @Inject constructor(
    private val analyticsClient: AnalyticsClient
): ViewModel() {

    companion object {
        private const val EXPLAINER_SCREEN_CLASS = "SubjectAccessRequestExplainerScreen"
        private const val EXPLAINER_SCREEN_NAME = "Subject Access Request Explainer"
        private const val EXPLAINER_TITLE = "Subject Access Request Explainer"
        private const val DISPLAY_SCREEN_CLASS = "SubjectAccessRequestDisplayScreen"
        private const val DISPLAY_SCREEN_NAME = "Subject Access Request Display"
        private const val DISPLAY_TITLE = "Subject Access Request Display"
        private const val SECTION = "Subject Access Request"
    }

    fun onExplainerPageView() {
        analyticsClient.screenView(
            screenClass = EXPLAINER_SCREEN_CLASS,
            screenName = EXPLAINER_SCREEN_NAME,
            title = EXPLAINER_TITLE
        )
    }

    fun onDisplayPageView() {
        analyticsClient.screenView(
            screenClass = DISPLAY_SCREEN_CLASS,
            screenName = DISPLAY_SCREEN_NAME,
            title = DISPLAY_TITLE
        )
    }

    fun onButtonClick(text: String) {
        analyticsClient.buttonClick(
            text = text,
            section = SECTION
        )
    }
}
