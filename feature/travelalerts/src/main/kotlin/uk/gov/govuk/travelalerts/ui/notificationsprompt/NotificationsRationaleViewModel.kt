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
import uk.gov.govuk.analytics.AnalyticsCoordinatorInterface
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.notifications.NotificationsPermissionResolver
import uk.gov.govuk.notifications.data.NotificationsRepo
import uk.gov.govuk.travelalerts.data.TravelAlertsRepo
import javax.inject.Inject

@HiltViewModel
class NotificationsRationaleViewModel @Inject constructor(
    private val notificationsRepo: NotificationsRepo,
    private val travelAlertsRepo: TravelAlertsRepo,
    private val permissionResolver: NotificationsPermissionResolver,
    private val analyticsCoordinator: AnalyticsCoordinatorInterface,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val KEY_UI_STATE = "uiState"
        private const val KEY_SELECTED_COUNTRY_SLUG = "selectedCountrySlug"
        private const val KEY_ORIGIN = "origin"
        private const val KEY_PENDING = "pending"
        private const val KEY_IS_COMPLETING = "isCompleting"
        private const val STATE_LOADING = "Loading"
        private const val STATE_DEFAULT = "Default"
        private const val STATE_ALERT = "Alert"
    }

    sealed class State {
        data object Loading : State()
        data object Default : State()
        data object Alert : State()
    }

    sealed class NavigationEvent {
        data class ExitToTopic(val error: Boolean) : NavigationEvent()
        data class ReturnToEdit(val slug: String, val error: Boolean, val notificationsEnabled: Boolean = false) : NavigationEvent()
    }

    enum class Origin {
        FOLLOW, EDIT
    }

    private enum class Pending { NONE, OS_PROMPT, SETTINGS }

    private val _uiState = MutableStateFlow<State>(
        savedStateHandle.get<String>(KEY_UI_STATE)?.let { stateName ->
            when (stateName) {
                STATE_LOADING -> State.Loading
                STATE_DEFAULT -> State.Default
                STATE_ALERT -> State.Alert
                else -> State.Loading
            }
        } ?: State.Loading
    )
    val uiState = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<NavigationEvent>()
    val navigationEvent: SharedFlow<NavigationEvent> = _navigationEvent

    private var selectedCountrySlug: String?
        get() = savedStateHandle[KEY_SELECTED_COUNTRY_SLUG]
        set(value) = savedStateHandle.set(KEY_SELECTED_COUNTRY_SLUG, value)

    private var origin: Origin
        get() = savedStateHandle.get<String>(KEY_ORIGIN)?.let { Origin.valueOf(it) } ?: Origin.FOLLOW
        set(value) = savedStateHandle.set(KEY_ORIGIN, value.name)

    private var pending: Pending
        get() = savedStateHandle.get<String>(KEY_PENDING)?.let { Pending.valueOf(it) } ?: Pending.NONE
        set(value) = savedStateHandle.set(KEY_PENDING, value.name)

    private var isCompleting: Boolean
        get() = savedStateHandle.get<Boolean>(KEY_IS_COMPLETING) ?: false
        set(value) = savedStateHandle.set(KEY_IS_COMPLETING, value)

    fun onPageView(countrySlug: String, origin: Origin = Origin.FOLLOW) {
        if (_uiState.value != State.Loading) return
        selectedCountrySlug = countrySlug
        this.origin = origin
        setUiState(State.Default)
        analyticsCoordinator.logEvent(
            "notifications_rationale_view",
            mapOf("origin" to origin.name)
        )
    }

    private fun setUiState(state: State) {
        _uiState.value = state
        val stateName = when (state) {
            State.Loading -> STATE_LOADING
            State.Default -> STATE_DEFAULT
            State.Alert -> STATE_ALERT
        }
        savedStateHandle[KEY_UI_STATE] = stateName
    }

    fun onNotNow(countrySlug: String) {
        analyticsCoordinator.logEvent(
            "notifications_rationale_not_now",
            mapOf("origin" to origin.name)
        )
        viewModelScope.launch {
            setUiState(State.Loading)
            val result = when (origin) {
                Origin.FOLLOW -> {
                    when (travelAlertsRepo.followCountry(countrySlug, notificationsEnabled = false)) {
                        is Result.Success -> NavigationEvent.ExitToTopic(error = false)
                        else -> NavigationEvent.ExitToTopic(error = true)
                    }
                }
                Origin.EDIT -> NavigationEvent.ReturnToEdit(countrySlug, error = false)
            }
            _navigationEvent.emit(result)
        }
    }

    @OptIn(ExperimentalPermissionsApi::class)
    fun onAgreeToContinue(
        permissionStatus: PermissionStatus,
        androidVersion: Int = Build.VERSION.SDK_INT
    ) {
        analyticsCoordinator.logEvent(
            "notifications_rationale_agree",
            mapOf("origin" to origin.name)
        )
        viewModelScope.launch {
            val path = permissionResolver.resolve(permissionStatus, androidVersion)

            when (path) {
                NotificationsPermissionResolver.PermissionPath.OS_PROMPT -> {
                    pending = Pending.OS_PROMPT
                    val granted = permissionResolver.requestOsPermission()
                    pending = Pending.NONE
                    complete(granted)
                }
                NotificationsPermissionResolver.PermissionPath.SETTINGS -> {
                    pending = Pending.SETTINGS
                    setUiState(State.Alert)
                }
                NotificationsPermissionResolver.PermissionPath.GRANTED -> {
                    notificationsRepo.giveConsent()
                    complete(true)
                }
            }
        }
    }

    fun onSettingsAlertContinue() {
        analyticsCoordinator.logEvent(
            "notifications_rationale_settings_continue",
            mapOf("origin" to origin.name)
        )
        pending = Pending.SETTINGS
        setUiState(State.Default)
    }

    fun onSettingsAlertCancelClicked() {
        analyticsCoordinator.logEvent(
            "notifications_rationale_settings_cancel",
            mapOf("origin" to origin.name)
        )
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
            val result = when (origin) {
                Origin.FOLLOW -> {
                    when (travelAlertsRepo.followCountry(selectedCountrySlug!!, notificationsEnabled = granted)) {
                        is Result.Success -> NavigationEvent.ExitToTopic(error = false)
                        else -> NavigationEvent.ExitToTopic(error = true)
                    }
                }
                Origin.EDIT -> {
                    when (travelAlertsRepo.toggleNotifications(selectedCountrySlug!!, enabled = granted)) {
                        is Result.Success -> NavigationEvent.ReturnToEdit(selectedCountrySlug!!, error = false, notificationsEnabled = granted)
                        else -> NavigationEvent.ReturnToEdit(selectedCountrySlug!!, error = true, notificationsEnabled = false)
                    }
                }
            }
            _navigationEvent.emit(result)
        }
    }
}
