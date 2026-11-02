package uk.gov.govuk.travelalerts.ui.widget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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
    data class UiState(
        val content: State = State.Loading,
        val followError: Boolean = false
    )

    sealed class State {
        data object Loading: State()
        data class Loaded(val rows: List<LoadedRow>): State()
        data object Empty: State()
        data object Error: State()
    }

    data class LoadedRow(val headline: String, val subtitle: String, val link: String, val url: String)

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    fun onFollowError() {
        _uiState.update { it.copy(followError = true) }
    }

    fun onDismissFollowError() {
        _uiState.update { it.copy(followError = false) }
    }

    fun onFollowCountryClick(text: String) {
        analyticsClient.widgetClick(
            text = text,
            external = false,
            section = SECTION
        )
    }

    fun onRowClick(row: LoadedRow) {
        analyticsClient.widgetClick(
            text = row.headline,
            url = row.url,
            external = true,
            section = SECTION
        )
    }

    fun onPageView() {
        viewModelScope.launch {
            _uiState.update { it.copy(content = State.Loading) }
            val groupsRes = travelAlertsRepo.getGroups()
            val countriesRes = travelAlertsRepo.getCountries()

            val content = if (groupsRes is Result.Success && countriesRes is Result.Success) {
                val countriesBySlug = countriesRes.value.associateBy(Country::slug)
                val rows = groupsRes.value.mapNotNull { group ->
                    countriesBySlug[group.group]?.let { country ->
                        LoadedRow(
                            headline = country.name,
                            subtitle = country.date.toRelativeDate(lowercase = true),
                            link = "govuk://app.gov.uk/web?url=$TRAVEL_ADVICE_BASE_URL${country.slug}",
                            url = "$TRAVEL_ADVICE_BASE_URL${country.slug}"
                        )
                    }
                }.sortedBy { it.headline }
                if (rows.isEmpty()) State.Empty else State.Loaded(rows)
            } else {
                State.Error
            }
            _uiState.update { it.copy(content = content) }
        }
    }

    companion object {
        private const val SECTION = "Travel Abroad Notifications"
        private const val TRAVEL_ADVICE_BASE_URL = "https://www.gov.uk/foreign-travel-advice/"
    }
}
