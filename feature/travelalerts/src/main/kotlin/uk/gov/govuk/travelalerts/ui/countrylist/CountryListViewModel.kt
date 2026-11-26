package uk.gov.govuk.travelalerts.ui.countrylist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.govkit.browser.Urls
import uk.gov.govuk.notifications.data.NotificationsRepo
import uk.gov.govuk.travelalerts.data.TravelAlertsRepo
import uk.gov.govuk.travelalerts.data.model.Country

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
        private const val CANCEL_TEXT = "Cancel"

        // How long the search query must be unchanged before a "no results" search is tracked
        internal const val NO_RESULTS_SEARCH_DEBOUNCE_MS = 500L
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
        data object DeviceOffline : State()
    }

    private val _uiState: MutableStateFlow<State> = MutableStateFlow(State.Loading)
    val uiState = _uiState.asStateFlow()

    sealed class NavigationEvent {
        data class NavigateToTopic(
            val error: Boolean = false
        ) : NavigationEvent()
        data class NavigateToNotificationsRationale(
            val countrySlug: String
        ) : NavigationEvent()
    }

    private val _navigationEvent = MutableSharedFlow<NavigationEvent>()
    val navigationEvent: SharedFlow<NavigationEvent> = _navigationEvent

    private var allCountries: List<Country> = emptyList()

    private val searchQueryChanges = MutableStateFlow("")

    init {
        trackSearchesWithNoResults()
    }

    @OptIn(FlowPreview::class)
    private fun trackSearchesWithNoResults() {
        viewModelScope.launch {
            searchQueryChanges
                .debounce(NO_RESULTS_SEARCH_DEBOUNCE_MS.milliseconds)
                .map { it.trim() }
                .distinctUntilChanged()
                .filter { it.isNotEmpty() }
                .collect { query ->
                    if (_uiState.value is State.Loaded && filterCountries(query).isEmpty()) {
                        analyticsClient.search(query, section = SEARCH_SECTION, extraParams = mapOf("results" to 0))
                    }
                }
        }
    }

    private fun updateLoaded(
        block: State.Loaded.() -> State.Loaded
    ) {
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
                is Result.DeviceOffline -> {
                    _uiState.value = State.DeviceOffline
                }
                else -> {
                    _uiState.value = State.Error
                }
            }
        }
    }

    fun onCountrySelected(
        country: Country
    ) {
        analyticsClient.toggleFunction(text = country.name, section = SECTION, action = "Add")
        val currentState = _uiState.value
        if (currentState is State.Loaded && currentState.searchQuery.isNotEmpty()) {
            analyticsClient.search(currentState.searchQuery, section = SEARCH_SECTION)
        }
        updateLoaded { copy(selectedCountry = country) }
    }

    /**
     * Called when the user closes the preference sheet without choosing an option (X, swipe,
     * tapping outside or back). Not called when the sheet closes after Not now or Get notifications.
     */
    fun onDismissPreferenceSheet() {
        val currentState = _uiState.value
        if (currentState is State.Loaded && currentState.selectedCountry != null) {
            analyticsClient.widgetClick(text = CANCEL_TEXT, external = false, section = SECTION)
        }
        updateLoaded { copy(selectedCountry = null) }
    }

    fun onCloseClick(
        text: String
    ) {
        analyticsClient.buttonClick(text = text, external = false, section = SECTION)
    }

    fun onNotNowNotifications(
        country: Country,
        text: String
    ) {
        analyticsClient.widgetClick(text = text, external = false, section = SECTION)
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

    fun onGetNotificationsClick(
        country: Country,
        text: String
    ) {
        analyticsClient.widgetClick(text = text, external = false, section = SECTION)
        if (notificationsRepo.permissionGranted()) {
            notificationsRepo.giveConsent()
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

    fun onHelpAndFeedbackClick(
        text: String
    ) {
        analyticsClient.buttonClick(
            text = text,
            url = Urls.HELP_AND_FEEDBACK_URL,
            external = true,
            section = SECTION
        )
    }

    fun onSearchQueryChange(
        query: String
    ) {
        updateLoaded { copy(countries = filterCountries(query), searchQuery = query) }
        searchQueryChanges.value = query
    }

    private fun filterCountries(
        query: String
    ): List<Country> {
        val q = query.lowercase().trim()
        return if (q.isEmpty()) {
            allCountries
        } else {
            allCountries.filter { country ->
                country.name.lowercase().contains(q) ||
                    country.synonyms.any { it.lowercase().contains(q) }
            }
        }
    }
}
