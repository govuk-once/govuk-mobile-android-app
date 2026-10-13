package uk.gov.govuk.travelalerts.ui.editcountries

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.notifications.data.NotificationsRepo
import uk.gov.govuk.travelalerts.data.TravelAlertsRepo
import uk.gov.govuk.travelalerts.data.model.Country
import uk.gov.govuk.travelalerts.data.model.Group
import uk.gov.govuk.travelalerts.data.model.Subgroup
import uk.gov.govuk.travelalerts.navigation.SHOW_ERROR_ARG
import javax.inject.Inject

@HiltViewModel
class EditCountriesViewModel @Inject constructor(
    private val travelAlertsRepo: TravelAlertsRepo,
    private val notificationsRepo: NotificationsRepo,
    private val analyticsClient: AnalyticsClient,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val SCREEN_CLASS = "EditCountriesScreen"
        private const val SCREEN_NAME = "Edit countries"
        private const val TITLE = "Edit countries"
    }

    sealed class State {
        data object Loading : State()
        data class Loaded(
            val countries: List<Country>,
            val groups: List<Group>,
            val selectedSlug: String? = null,
            val devicePermissionGranted: Boolean = false,
            val isTogglingNotifications: Boolean = false,
            val isUnfollowing: Boolean = false,
            val toggleError: Boolean = false,
            val unfollowError: Boolean = false,
            val followError: Boolean = false
        ) : State()
        data object Error : State()
    }

    sealed class NavigationEvent {
        data class ToNotificationsOptIn(val slug: String) : NavigationEvent()
    }

    private val _uiState: MutableStateFlow<State> = MutableStateFlow(State.Loading)
    val uiState = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<NavigationEvent>()
    val navigationEvent: SharedFlow<NavigationEvent> = _navigationEvent

    private var selectedSlug: String?
        get() = savedStateHandle["selectedSlug"]
        set(value) = savedStateHandle.set("selectedSlug", value)

    fun onPageView() {
        analyticsClient.screenView(
            screenClass = SCREEN_CLASS,
            screenName = SCREEN_NAME,
            title = TITLE
        )
        fetchFollowedCountries()
    }

    fun onResume() {
        viewModelScope.launch {
            val devicePermissionGranted = notificationsRepo.permissionGranted()
            updateLoaded { copy(devicePermissionGranted = devicePermissionGranted) }
        }
    }

    fun onRetry() {
        fetchFollowedCountries()
    }

    private fun updateLoaded(block: State.Loaded.() -> State.Loaded) {
        (_uiState.value as? State.Loaded)?.let { _uiState.value = it.block() }
    }

    fun toggleNotifications(slug: String, enabled: Boolean) {
        viewModelScope.launch {
            if (enabled && !notificationsRepo.permissionGranted()) {
                selectedSlug = slug
                _navigationEvent.emit(NavigationEvent.ToNotificationsOptIn(slug))
                return@launch
            }
            updateLoaded { copy(isTogglingNotifications = true, toggleError = false) }
            val result = travelAlertsRepo.toggleNotifications(slug, enabled)
            if (result is Result.Success) {
                val newSubgroup = if (enabled) Subgroup.INSTANT else Subgroup.NONE
                updateLoaded {
                    copy(
                        isTogglingNotifications = false,
                        groups = groups.map { group ->
                            if (group.group == slug) group.copy(subgroup = newSubgroup) else group
                        }
                    )
                }
            } else {
                updateLoaded {
                    copy(
                        isTogglingNotifications = false,
                        toggleError = true
                    )
                }
            }
        }
    }

    fun unfollowCountry(slug: String) {
        viewModelScope.launch {
            updateLoaded { copy(isUnfollowing = true, unfollowError = false) }
            val result = travelAlertsRepo.unfollowCountry(slug)
            if (result is Result.Success) {
                fetchFollowedCountries()
            } else {
                updateLoaded { copy(isUnfollowing = false, unfollowError = true) }
            }
        }
    }

    fun onOptInResult(slug: String, error: Boolean, notificationsEnabled: Boolean = false) {
        selectedSlug = slug
        if (error) {
            updateLoaded { copy(toggleError = true) }
        } else {
            updateLoaded {
                copy(
                    selectedSlug = slug,
                    groups = if (notificationsEnabled) {
                        groups.map { g -> if (g.group == slug) g.copy(subgroup = Subgroup.INSTANT) else g }
                    } else {
                        groups
                    }
                )
            }
            fetchFollowedCountries()
        }
    }

    fun notificationsEnabled(slug: String): Boolean {
        val loaded = _uiState.value as? State.Loaded ?: return false
        val subgroup = loaded.groups.find { it.group == slug }?.subgroup
        return subgroup == Subgroup.INSTANT && loaded.devicePermissionGranted
    }

    fun clearSelectedSlug() {
        selectedSlug = null
        updateLoaded { copy(selectedSlug = null) }
    }

    fun clearToggleError() = updateLoaded { copy(toggleError = false) }

    fun clearUnfollowError() = updateLoaded { copy(unfollowError = false) }

    fun clearFollowError() = updateLoaded { copy(followError = false) }

    private fun fetchFollowedCountries() {
        viewModelScope.launch {
            val isAlreadyLoaded = _uiState.value is State.Loaded
            if (!isAlreadyLoaded) {
                _uiState.value = State.Loading
            }
            val groupsResult = travelAlertsRepo.getGroups()
            val countriesResult = travelAlertsRepo.getCountries()
            if (groupsResult is Result.Success && countriesResult is Result.Success) {
                val countriesBySlug = countriesResult.value.associateBy(Country::slug)
                val followed = groupsResult.value
                    .mapNotNull { group -> countriesBySlug[group.group] }
                    .sortedBy { it.name }
                val followError = savedStateHandle.get<Boolean>(SHOW_ERROR_ARG) == true
                if (followError) savedStateHandle[SHOW_ERROR_ARG] = false
                val devicePermissionGranted = ((_uiState.value as? State.Loaded)?.devicePermissionGranted) ?: notificationsRepo.permissionGranted()
                _uiState.value = State.Loaded(followed, groupsResult.value, selectedSlug = selectedSlug, devicePermissionGranted = devicePermissionGranted, followError = followError)
            } else {
                _uiState.value = State.Error
            }
        }
    }
}
