package uk.gov.govuk.topics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.analytics.data.local.model.EcommerceEvent
import uk.gov.govuk.topics.data.TopicsRepo
import uk.gov.govuk.topics.extension.toTopicItemUi
import uk.gov.govuk.topics.ui.model.TopicItemUi
import javax.inject.Inject

internal data class TopicsWidgetUiState(
    val allTopics: List<TopicItemUi>,
    val yourTopics: List<TopicItemUi>
)

internal enum class TopicsCategory {
    YOUR, ALL
}

@HiltViewModel
internal class TopicsWidgetViewModel @Inject constructor(
    private val topicsRepo: TopicsRepo,
    private val analyticsClient: AnalyticsClient
): ViewModel() {

    private val _uiState: MutableStateFlow<TopicsWidgetUiState?> = MutableStateFlow(null)
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            topicsRepo.topics.collect { topics ->
                val mappedTopics = topics
                    .map{ topicItem -> topicItem.toTopicItemUi() }

                _uiState.value = TopicsWidgetUiState(
                    allTopics = mappedTopics,
                    yourTopics = mappedTopics.filter { it.isSelected }
                )
            }
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
        val items = mutableListOf<EcommerceEvent.Item>()

        val listCategory = mapCategory(category)

        topics.forEach { topic ->
            items += EcommerceEvent.Item(
                itemName = topic.title,
                locationId = topic.ref
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
                itemListName = listCategory,
                itemListId = listCategory,
                items = listOf(
                    EcommerceEvent.Item(
                        itemName = title,
                        locationId = ref
                    )
                ),
                totalItemCount = topicCount
            ),
            selectedItemIndex = selectedItemIndex
        )
    }

    private fun mapCategory(category: TopicsCategory): String {
        return when (category) {
            TopicsCategory.YOUR -> "Your topics"
            TopicsCategory.ALL -> "All topics"
        }
    }
}
