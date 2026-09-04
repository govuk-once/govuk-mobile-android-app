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
import uk.gov.govuk.travelalerts.data.TravelAlertsRepo
import uk.gov.govuk.travelalerts.data.model.Country
import javax.inject.Inject

@HiltViewModel
class CountryListViewModel @Inject constructor(
    private val travelAlertsRepo: TravelAlertsRepo,
    private val analyticsClient: AnalyticsClient
) : ViewModel() {

    companion object {
        private const val SCREEN_CLASS = "CountryListScreen"
        private const val SCREEN_NAME = "Follow a country"
        private const val TITLE = "Follow a country"
    }

    sealed class State {
        data object Loading : State()
        data class Loaded(val countries: List<Country>, val searchQuery: String = "") : State()
        data object Error : State()
    }

    private val _uiState: MutableStateFlow<State> = MutableStateFlow(State.Loading)
    val uiState = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<Unit>()
    val navigationEvent: SharedFlow<Unit> = _navigationEvent

    private var allCountries: List<Country> = emptyList()

    fun onPageView() {
        analyticsClient.screenView(
            screenClass = SCREEN_CLASS,
            screenName = SCREEN_NAME,
            title = TITLE
        )
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
        viewModelScope.launch {
            _uiState.value = State.Loading
            val result = travelAlertsRepo.subscribeToCountry(country.slug)
            if (result is Result.Success) {
                _navigationEvent.emit(Unit)
            } else {
                _uiState.value = State.Loaded(allCountries)
            }
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
        _uiState.value = State.Loaded(filtered, query)
    }
}
