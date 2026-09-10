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
        fetchFollowedCountries()
    }

    fun onRetry() {
        fetchFollowedCountries()
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
                _uiState.value = if (followed.isEmpty()) State.Error else State.Loaded(followed)
            } else {
                _uiState.value = State.Error
            }
        }
    }
}
