package uk.gov.govuk.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.notifications.data.NotificationsRepo
import javax.inject.Inject

@HiltViewModel
internal open class NotificationsViewModel @Inject constructor(
    private val analyticsClient: AnalyticsClient,
    private val notificationsRepo: NotificationsRepo
) : ViewModel() {

    companion object {
        private const val SCREEN_CLASS = "NotificationsOnboardingScreen"
        private const val TITLE = "NotificationsOnboardingScreen"
    }

    internal fun onPageView() {
        analyticsClient.screenView(
            screenClass = SCREEN_CLASS,
            screenName = TITLE,
            title = TITLE
        )
    }

    internal fun onAllowNotificationsClick(text: String, onCompleted: () -> Unit) {
        viewModelScope.launch {
            notificationsRepo.firstPermissionRequestCompleted()
            notificationsRepo.giveConsent()
            notificationsRepo.requestPermission()
            onCompleted()
        }
        analyticsClient.buttonClick(
            text = text
        )
    }

    internal fun onNotNowClick(text: String) {
        analyticsClient.buttonClick(
            text = text
        )
    }

    internal fun onGiveConsentClick(text: String, onCompleted: () -> Unit) {
        viewModelScope.launch {
            notificationsRepo.sendConsent()
            notificationsRepo.giveConsent()
            onCompleted()
        }
        analyticsClient.buttonClick(
            text = text
        )
    }

    internal fun onTurnOffNotificationsClick(text: String) {
        analyticsClient.buttonClick(
            text = text,
            external = true
        )
    }

    internal fun onPrivacyPolicyClick(text: String, url: String) {
        analyticsClient.buttonClick(
            text = text,
            url = url,
            external = true
        )
    }

    internal fun onContinueButtonClick(text: String) {
        viewModelScope.launch {
            notificationsRepo.sendRemoveConsent()
            notificationsRepo.removeConsent()
        }
        analyticsClient.buttonClick(text)
    }

    internal fun onCancelButtonClick(text: String) {
        analyticsClient.buttonClick(text)
    }
}
