package uk.gov.govuk.login

import android.os.Build
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.gov.govuk.R
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.data.AppRepo
import uk.gov.govuk.data.auth.AuthRepo
import javax.inject.Inject

@HiltViewModel
internal class BiometricSettingsViewModel @Inject constructor(
    private val appRepo: AppRepo,
    private val authRepo: AuthRepo,
    private val analyticsClient: AnalyticsClient
) : ViewModel() {

    companion object {
        private const val SCREEN_CLASS = "BiometricSettingsScreen"
        private const val SCREEN_NAME = "Biometric Settings"
        private const val TITLE = "Biometric Settings"
    }

    private val _uiState: MutableStateFlow<Boolean> = MutableStateFlow(authRepo.isUserSignedIn())
    val uiState = _uiState.asStateFlow()

    fun onPageView() {
        analyticsClient.screenView(
            screenClass = SCREEN_CLASS,
            screenName = SCREEN_NAME,
            title = TITLE
        )
    }

    fun onToggle(text: String, activity: FragmentActivity) {
        viewModelScope.launch {
            val action = if (authRepo.isUserSignedIn()) {
                authRepo.clear()
                BIOMETRICS_SETTINGS_DISABLE
            } else {
                appRepo.clearBiometricsSkipped()
                authRepo.persistRefreshToken(
                    activity = activity,
                    title = activity.getString(R.string.login_biometric_prompt_title)
                )
                BIOMETRICS_SETTINGS_ENABLE
            }

            analyticsClient.toggleFunction(
                text = text,
                section = BIOMETRICS_SECTION,
                action = action
            )

            _uiState.value = authRepo.isUserSignedIn()
        }
    }

    fun getDescriptionTwo(androidVersion: Int = Build.VERSION.SDK_INT): Int =
        if (androidVersion > Build.VERSION_CODES.Q) {
            R.string.biometric_settings_android_11_description_2
        } else {
            R.string.biometric_settings_android_10_description_2
        }
}