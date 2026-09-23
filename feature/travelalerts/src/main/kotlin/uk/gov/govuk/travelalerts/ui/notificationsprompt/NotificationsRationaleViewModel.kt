package uk.gov.govuk.travelalerts.ui.notificationsprompt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.notifications.data.NotificationsRepo
import uk.gov.govuk.travelalerts.data.TravelAlertsRepo
import javax.inject.Inject

@HiltViewModel
class NotificationsRationaleViewModel @Inject constructor(
    private val notificationsRepo: NotificationsRepo,
    private val travelAlertsRepo: TravelAlertsRepo
) : ViewModel() {

    sealed class State {
        data object Loading : State()
        data object Default : State()
        data object Alert : State()
        data object Error : State()
    }

    private val _uiState = MutableStateFlow<State>(State.Loading)
    val uiState = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<Unit>()
    val navigationEvent: SharedFlow<Unit> = _navigationEvent

    private var selectedCountrySlug: String? = null
    private var hasAgreedToContinue = false
    private var hasHandledResume = false

    fun onPageView(countrySlug: String) {
        if (_uiState.value != State.Loading) return
        selectedCountrySlug = countrySlug
        _uiState.value = State.Default
    }

    fun onNotNow(countrySlug: String) {
        viewModelScope.launch {
            _uiState.value = State.Loading
            when (travelAlertsRepo.followCountry(countrySlug, notificationsEnabled = false)) {
                is Result.Success -> {
                    _navigationEvent.emit(Unit)
                }

                else -> {
                    _uiState.value = State.Error
                }
            }
        }
    }

    @OptIn(ExperimentalPermissionsApi::class)
    fun onAgreeToContinue(permissionStatus: PermissionStatus) {
        hasAgreedToContinue = true
        viewModelScope.launch {
            notificationsRepo.firstPermissionRequestCompleted()
            notificationsRepo.giveConsent()
            notificationsRepo.requestPermission()
        }
    }

    fun onSettingsAlertCancel(countrySlug: String) {
        viewModelScope.launch {
            _uiState.value = State.Loading
            when (travelAlertsRepo.followCountry(countrySlug, notificationsEnabled = false)) {
                is Result.Success -> {
                    _navigationEvent.emit(Unit)
                }

                else -> {
                    _uiState.value = State.Error
                }
            }
        }
    }

    fun onSettingsAlertContinue() {
        // Continue button in the alert calls openDeviceNotificationsSettings, no action needed here
    }

    fun onResume(countrySlug: String) {
        if (!hasAgreedToContinue || hasHandledResume) return
        hasHandledResume = true
        viewModelScope.launch {
            _uiState.value = State.Loading
            if (notificationsRepo.permissionGranted()) {
                notificationsRepo.giveConsent()
                when (travelAlertsRepo.followCountry(countrySlug, notificationsEnabled = true)) {
                    is Result.Success -> {
                        _navigationEvent.emit(Unit)
                    }

                    else -> {
                        _uiState.value = State.Error
                    }
                }
            } else {
                _uiState.value = State.Default
                hasHandledResume = false
            }
        }
    }

    fun onDismissError() {
        _uiState.value = State.Default
    }
}
