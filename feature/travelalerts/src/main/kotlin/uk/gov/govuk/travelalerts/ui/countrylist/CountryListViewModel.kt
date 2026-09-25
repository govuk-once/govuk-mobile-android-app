package uk.gov.govuk.travelalerts.ui.countrylist

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
import javax.inject.Inject

@HiltViewModel
class CountryListViewModel @Inject constructor(
    private val travelAlertsRepo: TravelAlertsRepo,
    private val notificationsRepo: NotificationsRepo,
    private val analyticsClient: AnalyticsClient
) : ViewModel() {

    companion object {
        private const val SCREEN_CLASS = "CountryListScreen"
        private const val SCREEN_NAME = "Follow a country"
        private const val TITLE = "Follow a country"
        private const val SECTION = "Travel Abroad Notifications"
        private const val SEARCH_SECTION = "country_search"
    }

    sealed class State {
        data object Loading : State()
        data class Loaded(
            val countries: List<Country>,
            val searchQuery: String = "",
            val selectedCountry: Country? = null,
            val isSaving: Boolean = false
        ) : State()
        data object Error : State()
    }

    private val _uiState: MutableStateFlow<State> = MutableStateFlow(State.Loading)
    val uiState = _uiState.asStateFlow()

    sealed class NavigationEvent {
        data class NavigateToTopic(val error: Boolean = false) : NavigationEvent()
        data class NavigateToNotificationsRationale(val countrySlug: String) : NavigationEvent()
    }

    private val _navigationEvent = MutableSharedFlow<NavigationEvent>()
    val navigationEvent: SharedFlow<NavigationEvent> = _navigationEvent

    private var allCountries: List<Country> = emptyList()

    private fun updateLoaded(block: State.Loaded.() -> State.Loaded) {
        (_uiState.value as? State.Loaded)?.let { _uiState.value = it.block() }
    }

    fun onPageView() {
        analyticsClient.screenView(
            screenClass = SCREEN_CLASS,
            screenName = SCREEN_NAME,
            title = TITLE
        )
        if (allCountries.isNotEmpty()) return
        fetchCountries()
    }

    fun onRetry() {
        fetchCountries()
    }

    private fun fetchCountries() {
        viewModelScope.launch {
            allCountries = emptyList()
            _uiState.value = State.Loading
            when (val result = travelAlertsRepo.getCountries()) {
                is Result.Success -> {
                    if (result.value.isEmpty()) {
                        _uiState.value = State.Error
                    } else {
                        allCountries = result.value.sortedBy { it.name }
                        _uiState.value = State.Loaded(allCountries)
                    }
                }
                else -> _uiState.value = State.Error
            }
        }
    }

    fun onCountrySelected(country: Country) {
        analyticsClient.toggleFunction(text = country.name, section = SECTION, action = "Add")
        val currentState = _uiState.value
        if (currentState is State.Loaded && currentState.searchQuery.isNotEmpty()) {
            analyticsClient.search(currentState.searchQuery, section = SEARCH_SECTION)
        }
        updateLoaded { copy(selectedCountry = country) }
    }

    fun onDismissPreferenceSheet() {
        updateLoaded { copy(selectedCountry = null) }
    }

    fun onNotNowNotifications(country: Country) {
        viewModelScope.launch {
            updateLoaded { copy(isSaving = true) }
            when (travelAlertsRepo.followCountry(country.slug, notificationsEnabled = false)) {
                is Result.Success -> {
                    updateLoaded { copy(selectedCountry = null, isSaving = false) }
                    _navigationEvent.emit(NavigationEvent.NavigateToTopic())
                }
                else -> {
                    updateLoaded { copy(selectedCountry = null, isSaving = false) }
                    _navigationEvent.emit(NavigationEvent.NavigateToTopic(error = true))
                }
            }
        }
    }

    fun onGetNotificationsClick(country: Country) {
        if (notificationsRepo.permissionGranted()) {
            viewModelScope.launch {
                updateLoaded { copy(isSaving = true) }
                when (travelAlertsRepo.followCountry(country.slug, notificationsEnabled = true)) {
                    is Result.Success -> {
                        updateLoaded { copy(selectedCountry = null, isSaving = false) }
                        _navigationEvent.emit(NavigationEvent.NavigateToTopic())
                    }
                    else -> {
                        updateLoaded { copy(selectedCountry = null, isSaving = false) }
                        _navigationEvent.emit(NavigationEvent.NavigateToTopic(error = true))
                    }
                }
            }
        } else {
            updateLoaded { copy(selectedCountry = null) }
            viewModelScope.launch {
                _navigationEvent.emit(NavigationEvent.NavigateToNotificationsRationale(country.slug))
            }
        }
    }

    fun onSearchSubmitted() {
        val currentState = _uiState.value
        if (currentState is State.Loaded && currentState.searchQuery.isNotEmpty()) {
            analyticsClient.search(currentState.searchQuery, section = SEARCH_SECTION)
        }
    }

    fun onSearchQueryChange(query: String) {
        val q = query.lowercase()
        val filtered = if (q.isEmpty()) {
            allCountries
        } else {
            allCountries.filter { country ->
                country.name.lowercase().contains(q) ||
                    country.synonyms.any { it.lowercase().contains(q) }
            }
        }
        updateLoaded { copy(countries = filtered, searchQuery = query) }
    }
}
