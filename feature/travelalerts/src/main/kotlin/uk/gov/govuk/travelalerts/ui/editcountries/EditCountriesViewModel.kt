package uk.gov.govuk.travelalerts.ui.editcountries

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.travelalerts.data.TravelAlertsRepo
import uk.gov.govuk.travelalerts.data.model.Country
import uk.gov.govuk.travelalerts.data.model.Group
import uk.gov.govuk.travelalerts.navigation.SHOW_ERROR_ARG
import javax.inject.Inject

@HiltViewModel
class EditCountriesViewModel @Inject constructor(
    private val travelAlertsRepo: TravelAlertsRepo,
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
            val isTogglingNotifications: Boolean = false,
            val isUnfollowing: Boolean = false,
            val toggleError: String? = null,
            val unfollowError: String? = null,
            val followError: Boolean = false
        ) : State()
        data object Error : State()
    }

    private val _uiState: MutableStateFlow<State> = MutableStateFlow(State.Loading)
    val uiState = _uiState.asStateFlow()

    fun onPageView() {
        analyticsClient.screenView(
            screenClass = SCREEN_CLASS,
            screenName = SCREEN_NAME,
            title = TITLE
        )
        fetchFollowedCountries()
    }

    fun onRetry() {
        fetchFollowedCountries()
    }

    private fun updateLoaded(block: State.Loaded.() -> State.Loaded) {
        (_uiState.value as? State.Loaded)?.let { _uiState.value = it.block() }
    }

    fun toggleNotifications(slug: String, enabled: Boolean) {
        viewModelScope.launch {
            updateLoaded { copy(isTogglingNotifications = true, toggleError = null) }
            val result = travelAlertsRepo.toggleNotifications(slug, enabled)
            if (result is Result.Success) {
                val newSubgroup = if (enabled) "daily" else "none"
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
                        toggleError = "Failed to update notifications"
                    )
                }
            }
        }
    }

    fun unfollowCountry(slug: String, currentNotificationsEnabled: Boolean) {
        viewModelScope.launch {
            updateLoaded { copy(isUnfollowing = true, unfollowError = null) }
            val result = travelAlertsRepo.unfollowCountry(slug, currentNotificationsEnabled)
            if (result is Result.Success) {
                fetchFollowedCountries()
            } else {
                updateLoaded { copy(isUnfollowing = false, unfollowError = "Failed to unfollow country") }
            }
        }
    }

    fun clearToggleError() = updateLoaded { copy(toggleError = null) }

    fun clearUnfollowError() = updateLoaded { copy(unfollowError = null) }

    fun clearFollowError() = updateLoaded { copy(followError = false) }

    private fun fetchFollowedCountries() {
        viewModelScope.launch {
            _uiState.value = State.Loading
            val groupsResult = travelAlertsRepo.getGroups()
            val countriesResult = travelAlertsRepo.getCountries()
            if (groupsResult is Result.Success && countriesResult is Result.Success) {
                val countriesBySlug = countriesResult.value.associateBy(Country::slug)
                val followed = groupsResult.value
                    .mapNotNull { group -> countriesBySlug[group.group] }
                    .sortedBy { it.name }
                val followError = savedStateHandle.get<Boolean>(SHOW_ERROR_ARG) == true
                if (followError) savedStateHandle[SHOW_ERROR_ARG] = false
                _uiState.value = State.Loaded(followed, groupsResult.value, followError = followError)
            } else {
                _uiState.value = State.Error
            }
        }
    }
}
