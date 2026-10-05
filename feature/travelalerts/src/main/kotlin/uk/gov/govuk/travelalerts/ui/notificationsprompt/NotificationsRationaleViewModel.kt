package uk.gov.govuk.travelalerts.ui.notificationsprompt

import android.os.Build
import androidx.compose.runtime.getValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.shouldShowRationale
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
    private val travelAlertsRepo: TravelAlertsRepo,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    sealed class State {
        data object Loading : State()
        data object Default : State()
        data object Alert : State()
    }

    private enum class Pending { NONE, OS_PROMPT, SETTINGS }

    private val _uiState = MutableStateFlow<State>(
        savedStateHandle.get<String>("uiState")?.let { stateName ->
            when (stateName) {
                "Loading" -> State.Loading
                "Default" -> State.Default
                "Alert" -> State.Alert
                else -> State.Loading
            }
        } ?: State.Loading
    )
    val uiState = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<Boolean>()
    val navigationEvent: SharedFlow<Boolean> = _navigationEvent

    private var selectedCountrySlug: String?
        get() = savedStateHandle["selectedCountrySlug"]
        set(value) = savedStateHandle.set("selectedCountrySlug", value)

    private var pending: Pending
        get() = savedStateHandle.get<String>("pending")?.let { Pending.valueOf(it) } ?: Pending.NONE
        set(value) = savedStateHandle.set("pending", value.name)

    private var isCompleting: Boolean
        get() = savedStateHandle.get<Boolean>("isCompleting") ?: false
        set(value) = savedStateHandle.set("isCompleting", value)

    fun onPageView(countrySlug: String) {
        if (_uiState.value != State.Loading) return
        selectedCountrySlug = countrySlug
        setUiState(State.Default)
    }

    private fun setUiState(state: State) {
        _uiState.value = state
        val stateName = when (state) {
            State.Loading -> "Loading"
            State.Default -> "Default"
            State.Alert -> "Alert"
        }
        savedStateHandle["uiState"] = stateName
    }

    fun onNotNow(countrySlug: String) {
        viewModelScope.launch {
            setUiState(State.Loading)
            when (travelAlertsRepo.followCountry(countrySlug, notificationsEnabled = false)) {
                is Result.Success -> {
                    _navigationEvent.emit(false)
                }

                else -> {
                    _navigationEvent.emit(true)
                }
            }
        }
    }

    @OptIn(ExperimentalPermissionsApi::class)
    fun onAgreeToContinue(
        permissionStatus: PermissionStatus,
        androidVersion: Int = Build.VERSION.SDK_INT
    ) {
        viewModelScope.launch {
            val isDefault = androidVersion >= Build.VERSION_CODES.TIRAMISU &&
                !permissionStatus.isGranted &&
                (!notificationsRepo.isFirstPermissionRequestCompleted() || permissionStatus.shouldShowRationale)

            if (isDefault) {
                pending = Pending.OS_PROMPT
                notificationsRepo.firstPermissionRequestCompleted()
                notificationsRepo.giveConsent()
                val granted = notificationsRepo.requestPermission()
                pending = Pending.NONE
                complete(granted)
            } else {
                pending = Pending.SETTINGS
                setUiState(State.Alert)
            }
        }
    }

    fun onSettingsAlertContinue() {
        pending = Pending.SETTINGS
        setUiState(State.Default)
    }

    fun onSettingsAlertCancelClicked() {
        // Analytics only
    }

    fun onSettingsAlertDismissed() {
        // If Continue was tapped (pending==SETTINGS and state==Default), the user is in the
        // Settings app - onDismiss fires as the dialog closes, so do nothing here.
        if (pending == Pending.SETTINGS && _uiState.value != State.Alert) {
            return
        }
        selectedCountrySlug?.let { complete(false) }
    }

    fun onResume(countrySlug: String) {
        // pending == SETTINGS while the dialog is showing AND while the user is in the
        // Settings app. ON_RESUME fires for both rotation and returning from Settings.
        // Guard against the dialog case: if state is Alert, the user hasn't gone to Settings yet.
        if (pending != Pending.SETTINGS || _uiState.value == State.Alert) return
        viewModelScope.launch {
            if (notificationsRepo.permissionGranted()) {
                notificationsRepo.giveConsent()
                complete(true)
            } else {
                setUiState(State.Default)
            }
        }
    }

    private fun complete(granted: Boolean) {
        if (isCompleting) return
        isCompleting = true
        viewModelScope.launch {
            setUiState(State.Loading)
            when (travelAlertsRepo.followCountry(selectedCountrySlug!!, notificationsEnabled = granted)) {
                is Result.Success -> {
                    _navigationEvent.emit(false)
                }

                else -> {
                    _navigationEvent.emit(true)
                }
            }
        }
    }
}
