package uk.gov.govuk.visited

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.visited.data.VisitedRepo
import uk.gov.govuk.visited.data.transformVisitedItems
import uk.gov.govuk.visited.ui.model.VisitedUi
import java.time.LocalDate
import javax.inject.Inject

internal sealed interface VisitedWidgetUiState {
    data class Visited(val items: List<VisitedUi>) : VisitedWidgetUiState
    data object NoVisited : VisitedWidgetUiState
}

@HiltViewModel
internal class VisitedWidgetViewModel @Inject constructor(
    private val visitedRepo: VisitedRepo,
    private val visited: Visited,
    private val analyticsClient: AnalyticsClient
) : ViewModel() {

    private val _uiState: MutableStateFlow<VisitedWidgetUiState?> = MutableStateFlow(null)
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            visitedRepo.visitedItems.collect { visitedItems ->
                val allVisitedItems = transformVisitedItems(visitedItems, LocalDate.now())
                _uiState.value =
                    if (allVisitedItems.isEmpty()) {
                        VisitedWidgetUiState.NoVisited
                    } else {
                        VisitedWidgetUiState.Visited(allVisitedItems.getTopThree())
                    }
            }
        }
    }

    private fun Map<String, List<VisitedUi>>.getTopThree(): List<VisitedUi> {
        return this.values.flatten().take(3)
    }

    fun onVisitedItemClicked(title: String, url: String) {
        viewModelScope.launch {
            visited.visitableItemClick(title = title, url = url)
        }
        analyticsClient.visitedItemClick(text = title, url = url)
    }
}
