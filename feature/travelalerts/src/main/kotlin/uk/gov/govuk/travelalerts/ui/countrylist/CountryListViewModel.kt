package uk.gov.govuk.travelalerts.ui.countrylist

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
        data class Loaded(val countries: List<Country>) : State()
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
        viewModelScope.launch {
            _uiState.value = State.Loading
            when (val result = travelAlertsRepo.getCountries()) {
                is Result.Success -> {
                    if (result.value.isEmpty()) {
                        _uiState.value = State.Error
                    } else {
                        val sorted = result.value.sortedBy { it.name }
                        _uiState.value = State.Loaded(sorted)
                    }
                }
                else -> _uiState.value = State.Error
            }
        }
    }
}
