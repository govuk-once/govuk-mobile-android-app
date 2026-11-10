package uk.gov.govuk.visited

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.visited.data.VisitedRepo
import uk.gov.govuk.visited.data.store.VisitedLocalDataSource
import uk.gov.govuk.visited.data.transformVisitedItems
import uk.gov.govuk.visited.ui.model.VisitedUi
import java.time.LocalDate
import javax.inject.Inject

internal data class VisitedUiState(
    val visited: Map<String, List<VisitedUi>>?,
    val hasSelectedItems: Boolean,
    val hasAllSelectedItems: Boolean
)

@HiltViewModel
internal class VisitedViewModel @Inject constructor(
    private val visitedRepo: VisitedRepo,
    private val visitedLocalDataSource: VisitedLocalDataSource,
    private val visited: Visited,
    private val analyticsClient: AnalyticsClient
) : ViewModel() {

    companion object {
        private const val VIEW_SCREEN_CLASS = "VisitedScreen"
        private const val SCREEN_NAME = "Pages you've visited"
        private const val SCREEN_TITLE = "Pages you've visited"
        private const val REMOVE_ACTION = "Remove"
        private const val REMOVE_ALL_ACTION = "Remove all"
        private const val DONE_BUTTON = "Done"
    }

    private val _uiState: MutableStateFlow<VisitedUiState?> = MutableStateFlow(null)
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            visitedRepo.visitedItems.collect { visitedItems ->
                val allVisitedItems = transformVisitedItems(visitedItems, LocalDate.now())
                _uiState.value = VisitedUiState(
                    visited = allVisitedItems,
                    hasSelectedItems = false,
                    hasAllSelectedItems = false
                )
            }
        }
    }

    fun onPageView() {
        analyticsClient.screenView(
            screenClass = VIEW_SCREEN_CLASS,
            screenName = SCREEN_NAME,
            title = SCREEN_TITLE
        )
    }

    fun onRemoveVisitedItem(title: String) {
        analyticsClient.buttonFunction(
            text = title,
            section = SCREEN_NAME,
            action = REMOVE_ACTION
        )
    }


    fun onRemoveAllVisitedItems() {
        analyticsClient.buttonFunction(
            text = "",
            section = SCREEN_NAME,
            action = REMOVE_ALL_ACTION
        )
    }

    fun onDoneClick() {
        analyticsClient.buttonFunction(
            text = "",
            section = SCREEN_NAME,
            action = DONE_BUTTON
        )
    }

    fun onVisitedItemClicked(title: String, url: String) {
        viewModelScope.launch {
            visited.visitableItemClick(title = title, url = url)
        }
        analyticsClient.visitedItemClick(text = title, url = url)
    }

    fun onVisitedItemRemoveClicked(title: String, url: String) {
        viewModelScope.launch {
            visitedLocalDataSource.remove(title, url)
            onRemoveVisitedItem(title)
        }
    }

    fun onRemoveAllVisitedItemsClicked() {
        _uiState.value?.visited?.forEach { (_, visitedItems) ->
            visitedItems.forEach { visitedItem ->
                viewModelScope.launch {
                    visitedLocalDataSource.remove(visitedItem.title, visitedItem.url)
                    onRemoveVisitedItem(visitedItem.title)
                }
            }
        }
        onRemoveAllVisitedItems()
    }
}
