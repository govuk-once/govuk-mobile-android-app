package uk.gov.govuk.travelalerts.ui.widget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.govkit.date.toRelativeDate
import uk.gov.govuk.travelalerts.data.TravelAlertsRepo
import uk.gov.govuk.travelalerts.data.model.Country
import javax.inject.Inject

@HiltViewModel
class TravelAlertsWidgetViewModel @Inject constructor(
    private val travelAlertsRepo: TravelAlertsRepo,
    private val analyticsClient: AnalyticsClient
) : ViewModel() {
    sealed class State {
        data object Loading: State()
        data class Loaded(val rows: List<LoadedRow>): State()
        data object Empty: State()
        data object Error: State()
    }

    data class LoadedRow(val headline: String, val subtitle: String, val link: String)

    private val _uiState: MutableStateFlow<State> =
        MutableStateFlow(State.Loading)
    val uiState = _uiState.asStateFlow()

    fun onRowClick(row: LoadedRow) {
        analyticsClient.widgetClick(
            text = row.headline,
            url = row.link,
            external = true,
            section = SECTION
        )
    }

    fun onPageView() {
        viewModelScope.launch {
            _uiState.value = State.Loading
            val groupsRes = travelAlertsRepo.getGroups()
            val countriesRes = travelAlertsRepo.getCountries()

            if (groupsRes is Result.Success && countriesRes is Result.Success) {
                val countriesBySlug = countriesRes.value.associateBy(Country::slug)
                val rows = groupsRes.value.mapNotNull { group ->
                    countriesBySlug[group.group]?.let { country ->
                        LoadedRow(
                            headline = country.name,
                            subtitle = country.date.toRelativeDate(),
                            // TODO Would rather this wasn't hard-coded, but not 100% if it's coming back whole off the API
                            link = "https://www.gov.uk/foreign-travel-advice/${country.slug}"
                        )
                    }
                }.sortedBy { it.headline }
                _uiState.value = if (rows.isEmpty()) State.Empty else State.Loaded(rows)
            } else {
                _uiState.value = State.Error
            }
        }
    }

    companion object {
        private const val SECTION = "Travel Abroad Notifications"
    }
}