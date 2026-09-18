package uk.gov.govuk.travelalerts.ui.editcountries

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
import javax.inject.Inject

@HiltViewModel
class EditCountriesViewModel @Inject constructor(
    private val travelAlertsRepo: TravelAlertsRepo,
    private val analyticsClient: AnalyticsClient
) : ViewModel() {

    companion object {
        private const val SCREEN_CLASS = "EditCountriesScreen"
        private const val SCREEN_NAME = "Edit countries"
        private const val TITLE = "Edit countries"
    }

    sealed class State {
        data object Loading : State()
        data class Loaded(val countries: List<Country>, val groups: List<Group>) : State()
        data object Error : State()
    }

    private val _uiState: MutableStateFlow<State> = MutableStateFlow(State.Loading)
    val uiState = _uiState.asStateFlow()

    private val _isTogglingNotifications: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isTogglingNotifications = _isTogglingNotifications.asStateFlow()

    private val _isUnfollowing: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isUnfollowing = _isUnfollowing.asStateFlow()

    private val _toggleError: MutableStateFlow<String?> = MutableStateFlow(null)
    val toggleError = _toggleError.asStateFlow()

    private val _unfollowError: MutableStateFlow<String?> = MutableStateFlow(null)
    val unfollowError = _unfollowError.asStateFlow()

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

    fun toggleNotifications(slug: String, enabled: Boolean) {
        viewModelScope.launch {
            _isTogglingNotifications.value = true
            _toggleError.value = null
            val result = travelAlertsRepo.toggleNotifications(slug, enabled)
            _isTogglingNotifications.value = false
            if (result !is Result.Success) {
                _toggleError.value = "Failed to update notifications"
            }
        }
    }

    fun unfollowCountry(slug: String, currentNotificationsEnabled: Boolean) {
        viewModelScope.launch {
            _isUnfollowing.value = true
            _unfollowError.value = null
            val result = travelAlertsRepo.unfollowCountry(slug, currentNotificationsEnabled)
            _isUnfollowing.value = false
            if (result is Result.Success) {
                fetchFollowedCountries()
            } else {
                _unfollowError.value = "Failed to unfollow country"
            }
        }
    }

    fun clearToggleError() {
        _toggleError.value = null
    }

    fun clearUnfollowError() {
        _unfollowError.value = null
    }

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
                _uiState.value = if (followed.isEmpty()) State.Error else State.Loaded(followed, groupsResult.value)
            } else {
                _uiState.value = State.Error
            }
        }
    }
}
