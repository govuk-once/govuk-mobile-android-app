package uk.gov.govuk.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.data.auth.AuthRepo
import uk.gov.govuk.data.auth.ErrorEvent
import uk.gov.govuk.data.auth.ErrorEvent.UnableToSignOutError
import uk.gov.govuk.settings.NavigationEvent.Error
import uk.gov.govuk.settings.NavigationEvent.Success
import javax.inject.Inject

sealed class NavigationEvent {
    data object Success: NavigationEvent()
    data class Error(val error: ErrorEvent): NavigationEvent()
}

@HiltViewModel
class SignOutViewModel @Inject constructor(
    private val authRepo: AuthRepo,
    private val analyticsClient: AnalyticsClient
): ViewModel() {

    private val _navigationEvent = MutableSharedFlow<NavigationEvent>()
    val navigationEvent: SharedFlow<NavigationEvent> = _navigationEvent

    companion object {
        private const val SCREEN_CLASS = "SignOutScreen"
        private const val ERROR_SCREEN_CLASS = "SignOutErrorScreen"
        private const val SCREEN_NAME = "Sign Out"
        private const val TITLE = "Sign Out"

        private const val SECTION = "Settings"
    }

    fun onPageView() {
        analyticsClient.screenView(
            screenClass = SCREEN_CLASS,
            screenName = SCREEN_NAME,
            title = TITLE
        )
    }

    fun onErrorPageView() {
        analyticsClient.screenView(
            screenClass = ERROR_SCREEN_CLASS,
            screenName = SCREEN_NAME,
            title = TITLE
        )
    }

    fun onBack(text: String) {
        analyticsClient.buttonClick(
            text = text,
            section = SECTION
        )
    }

    fun onSignOut(text: String) {
        analyticsClient.buttonClick(
            text = text,
            section = SECTION
        )

        viewModelScope.launch {
            if (authRepo.clear()) {
                _navigationEvent.emit(Success)
            } else {
                _navigationEvent.emit(Error(UnableToSignOutError))
            }
        }
    }
}
