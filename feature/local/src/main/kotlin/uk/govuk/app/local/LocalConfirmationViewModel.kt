package uk.govuk.app.local

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import uk.gov.govuk.analytics.AnalyticsClient
import uk.govuk.app.local.data.LocalRepo
import javax.inject.Inject

@HiltViewModel
internal class LocalConfirmationViewModel @Inject constructor(
    private val localRepo: LocalRepo,
    private val analyticsClient: AnalyticsClient
): ViewModel() {

    companion object {
        private const val SCREEN_CLASS = "LocalConfirmationScreen"
        private const val SCREEN_NAME = "Local Confirmation"
        private const val TITLE = "Local Confirmation"
    }

    internal val localAuthority
        get() = localRepo.cachedLocalAuthority

    fun onPageView() {
        analyticsClient.screenView(
            screenClass = SCREEN_CLASS,
            screenName = SCREEN_NAME,
            title = TITLE
        )
    }

    fun onDone(text: String) {
        analyticsClient.buttonClick(
            text = text,
            section = SECTION
        )

        viewModelScope.launch {
            localRepo.selectLocalAuthority()
        }
    }
}