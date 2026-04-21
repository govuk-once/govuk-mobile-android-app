package uk.gov.govuk.topics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.analytics.data.local.model.EcommerceEvent
import uk.gov.govuk.topics.data.TopicsRepo
import uk.gov.govuk.topics.extension.toTopicItemUi
import uk.gov.govuk.topics.ui.model.TopicItemUi
import javax.inject.Inject

internal data class TopicsWidgetUiState(
    val allTopics: List<TopicItemUi>,
    val yourTopics: List<TopicItemUi>,
    val selectedCategory: TopicsCategory = TopicsCategory.YOUR
)

internal enum class TopicsCategory {
    YOUR, ALL
}

@HiltViewModel
internal class TopicsWidgetViewModel @Inject constructor(
    private val topicsRepo: TopicsRepo,
    private val analyticsClient: AnalyticsClient,
) : ViewModel() {

    val uiState: StateFlow<TopicsWidgetUiState?> = combine(
        topicsRepo.topics,
        topicsRepo.selectedCategoryFlow
    ) { topics, category ->
        val mappedTopics = topics.map { it.toTopicItemUi() }

        TopicsWidgetUiState(
            allTopics = mappedTopics,
            yourTopics = mappedTopics.filter { it.isSelected },
            selectedCategory = category
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    fun onCategoryChange(category: TopicsCategory) {
        viewModelScope.launch {
            topicsRepo.setSelectedCategory(category)
        }
    }

    fun onTopicSelectClick(
        category: TopicsCategory,
        title: String,
        ref: String,
        selectedItemIndex: Int,
        topicCount: Int
    ) {
        sendSelectItemEvent(
            category = category,
            title = title,
            ref = ref,
            selectedItemIndex = selectedItemIndex,
            topicCount = topicCount
        )
    }

    fun onView(category: TopicsCategory, topics: List<TopicItemUi>) {
        sendViewItemListEvent(category, topics)
    }

    private fun sendViewItemListEvent(category: TopicsCategory, topics: List<TopicItemUi>) {
        val listCategory = mapCategory(category)

        val items = topics.map {
            EcommerceEvent.Item(
                itemName = it.title,
                locationId = it.ref
            )
        }

        analyticsClient.viewItemListEvent(
            ecommerceEvent = EcommerceEvent(
                itemListName = listCategory,
                itemListId = listCategory,
                items = items,
                totalItemCount = items.size
            )
        )
    }

    private fun sendSelectItemEvent(
        category: TopicsCategory,
        title: String,
        ref: String,
        selectedItemIndex: Int,
        topicCount: Int
    ) {
        val listCategory = mapCategory(category)

        analyticsClient.selectItemEvent(
            ecommerceEvent = EcommerceEvent(
                itemListName = listCategory, itemListId = listCategory, items = listOf(
                    EcommerceEvent.Item(
                        itemName = title, locationId = ref
                    )
                ), totalItemCount = topicCount
            ), selectedItemIndex = selectedItemIndex
        )
    }

    private fun mapCategory(category: TopicsCategory): String {
        return when (category) {
            TopicsCategory.YOUR -> "Your topics"
            TopicsCategory.ALL -> "All topics"
        }
    }
}
