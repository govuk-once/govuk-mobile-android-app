package uk.gov.govuk.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.config.data.ConfigRepo
import uk.gov.govuk.config.data.flags.FlagRepo
import uk.gov.govuk.data.auth.AuthRepo
import uk.gov.govuk.settings.BuildConfig.ACCESSIBILITY_STATEMENT_EVENT
import uk.gov.govuk.settings.BuildConfig.ACCESSIBILITY_STATEMENT_URL
import uk.gov.govuk.settings.BuildConfig.ACCOUNT_EVENT
import uk.gov.govuk.settings.BuildConfig.ACCOUNT_URL
import uk.gov.govuk.settings.BuildConfig.HELP_AND_FEEDBACK_EVENT
import uk.gov.govuk.settings.BuildConfig.HELP_AND_FEEDBACK_URL
import uk.gov.govuk.settings.BuildConfig.NOTIFICATIONS_PERMISSION_EVENT
import uk.gov.govuk.settings.BuildConfig.OPEN_SOURCE_LICENCE_EVENT
import uk.gov.govuk.settings.BuildConfig.PRIVACY_POLICY_EVENT
import uk.gov.govuk.settings.BuildConfig.PRIVACY_POLICY_URL
import uk.gov.govuk.settings.BuildConfig.SIGN_OUT_EVENT
import uk.gov.govuk.settings.BuildConfig.TERMS_AND_CONDITIONS_EVENT
import uk.gov.govuk.settings.BuildConfig.TERMS_AND_CONDITIONS_URL
import javax.inject.Inject

internal data class SettingsUiState(
    val userEmail: String,
    val isNotificationsEnabled: Boolean,
    val isAuthenticationEnabled: Boolean,
    val isAnalyticsEnabled: Boolean
)

@HiltViewModel
internal class SettingsViewModel @Inject constructor(
    authRepo: AuthRepo,
    flagRepo: FlagRepo,
    private val analyticsClient: AnalyticsClient,
    private val configRepo: ConfigRepo
): ViewModel() {

    companion object {
        private const val SCREEN_CLASS = "SettingsScreen"
        private const val SCREEN_NAME = "Settings"
        private const val TITLE = "Settings"
    }

    private val _uiState: MutableStateFlow<SettingsUiState?> = MutableStateFlow(null)
    val uiState = _uiState.asStateFlow()

    init {
        _uiState.value = SettingsUiState(
            userEmail = authRepo.getUserEmail(),
            isNotificationsEnabled = flagRepo.isNotificationsEnabled(),
            isAuthenticationEnabled = authRepo.isAuthenticationEnabled(),
            isAnalyticsEnabled = analyticsClient.isAnalyticsEnabled()
        )
    }

    fun onPageView() {
        analyticsClient.screenView(
            screenClass = SCREEN_CLASS,
            screenName = SCREEN_NAME,
            title = TITLE
        )
    }

    fun onAccount() {
        analyticsClient.settingsItemClick(
            text = ACCOUNT_EVENT,
            url = ACCOUNT_URL
        )
    }

    fun onSignOut() {
        analyticsClient.settingsItemClick(
            text = SIGN_OUT_EVENT,
            external = false
        )
    }

    fun onLicenseView() {
        analyticsClient.screenView(
            screenClass = OPEN_SOURCE_LICENCE_EVENT,
            screenName = OPEN_SOURCE_LICENCE_EVENT,
            title = OPEN_SOURCE_LICENCE_EVENT
        )
    }

    fun onHelpAndFeedbackView() {
        analyticsClient.settingsItemClick(
            text = HELP_AND_FEEDBACK_EVENT,
            url = HELP_AND_FEEDBACK_URL
        )
    }

    fun onPrivacyPolicyView() {
        analyticsClient.settingsItemClick(
            text = PRIVACY_POLICY_EVENT,
            url = PRIVACY_POLICY_URL
        )
    }

    fun onAccessibilityStatementView() {
        analyticsClient.settingsItemClick(
            text = ACCESSIBILITY_STATEMENT_EVENT,
            url = ACCESSIBILITY_STATEMENT_URL
        )
    }

    fun onNotificationsClick() {
        analyticsClient.screenView(
            screenClass = NOTIFICATIONS_PERMISSION_EVENT,
            screenName = NOTIFICATIONS_PERMISSION_EVENT,
            title = NOTIFICATIONS_PERMISSION_EVENT
        )
    }

    fun onBiometricsClick(text: String) {
        analyticsClient.settingsItemClick(
            text = text,
            external = false
        )
    }

    fun onTermsAndConditionsView() {
        analyticsClient.settingsItemClick(
            text = TERMS_AND_CONDITIONS_EVENT,
            url = TERMS_AND_CONDITIONS_URL
        )
    }

    fun onAnalyticsConsentChanged(enabled: Boolean) {
        viewModelScope.launch {
            if (enabled) {
                analyticsClient.enable()
            } else {
                analyticsClient.disable()
                configRepo.clearRemoteConfigValues()
            }
            _uiState.update { current ->
                current?.copy(isAnalyticsEnabled = enabled)
            }
        }
    }

    fun onButtonClick(text: String) {
        analyticsClient.buttonClick(text)
    }
}
