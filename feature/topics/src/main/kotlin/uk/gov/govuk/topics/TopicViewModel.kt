package uk.gov.govuk.topics

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.analytics.data.local.model.EcommerceEvent
import uk.gov.govuk.data.model.Result.DeviceOffline
import uk.gov.govuk.data.model.Result.Success
import uk.gov.govuk.topics.data.TopicsRepo
import uk.gov.govuk.topics.extension.toTopicUi
import uk.gov.govuk.topics.navigation.TOPIC_REF_ARG
import uk.gov.govuk.topics.navigation.TOPIC_SUBTOPIC_ARG
import uk.gov.govuk.topics.ui.model.TopicUi
import uk.gov.govuk.visited.Visited
import javax.inject.Inject

@HiltViewModel
internal class TopicViewModel @Inject constructor(
    private val topicsRepo: TopicsRepo,
    private val analyticsClient: AnalyticsClient,
    private val visited: Visited,
    private val savedStateHandle: SavedStateHandle
): ViewModel() {

    companion object {
        private const val SCREEN_CLASS = "TopicScreen"
        private const val SUBTOPIC_SECTION = "Sub topics"
        private const val LIST_NAME = "Topics"
        private const val POPULAR_PAGES_TITLE = "Popular pages"
        private const val STEP_BY_STEPS_TITLE = "Step by step guides"
        private const val BROWSE_TITLE = "Browse"
        private const val SERVICES_TITLE = "Services and information"
        private const val MAX_POPULAR_PAGES = 4
        private const val MAX_STEP_BY_STEPS = 3
    }

    private val _uiState: MutableStateFlow<TopicUiState?> = MutableStateFlow(null)
    val uiState = _uiState.asStateFlow()

    init {
        getTopic()
    }

    internal fun getTopic() {
        savedStateHandle.get<String>(TOPIC_REF_ARG)?.let { ref ->
            val isSubtopic = savedStateHandle.get<Boolean>(TOPIC_SUBTOPIC_ARG) == true
            viewModelScope.launch {
                val result = topicsRepo.getTopic(ref)
                _uiState.value = when (result) {
                    is Success -> {
                        TopicUiState.Default(
                            result.value.toTopicUi(
                                MAX_POPULAR_PAGES,
                                MAX_STEP_BY_STEPS,
                                isSubtopic
                            )
                        )
                    }
                    is DeviceOffline -> TopicUiState.Offline(ref)
                    else -> TopicUiState.ServiceError(ref)
                }
            }
        }
    }

    fun onPageView(
        topicUi: TopicUi? = null,
        title: String
    ) {
        analyticsClient.screenView(
            screenClass = SCREEN_CLASS,
            screenName = title,
            title = title
        )

        if (topicUi != null) {
            sendViewItemListEvent(topicUi = topicUi, title = title)
        }
    }

    fun onContentClick(
        title: String,
        section: String,
        text: String,
        url: String,
        selectedItemIndex: Int,
        totalItemCount: Int
    ) {
        analyticsClient.buttonClick(
            text = text,
            url = url,
            external = true,
            section = section
        )

        sendSelectItemEvent(
            title = title,
            section = section,
            text = text,
            url = url,
            selectedItemIndex = selectedItemIndex,
            totalItemCount = totalItemCount
        )

        viewModelScope.launch {
            visited.visitableItemClick(title = text, url = url)
        }
    }

    fun onSeeAllClick(
        section: String,
        text: String
    ) {
        analyticsClient.buttonClick(
            text = text,
            external = false,
            section = section
        )
    }

    fun onSubtopicClick(
        text: String,
        selectedItemIndex: Int,
        totalItemCount: Int
    ) {
        analyticsClient.buttonClick(
            text = text,
            external = false,
            section = SUBTOPIC_SECTION
        )

        sendSelectItemEvent(
            title = null,
            section = SUBTOPIC_SECTION,
            text = text,
            url = null,
            selectedItemIndex = selectedItemIndex,
            totalItemCount = totalItemCount
        )
    }

    private fun sendSelectItemEvent(
        section: String,
        text: String,
        title: String?,
        url: String?,
        selectedItemIndex: Int,
        totalItemCount: Int
    ) {
        analyticsClient.selectItemEvent(
            ecommerceEvent = EcommerceEvent(
                itemListName = LIST_NAME,
                itemListId = title ?: "",
                items = listOf(
                    EcommerceEvent.Item(
                        itemName = text,
                        itemCategory = section,
                        locationId = url ?: ""
                    )
                ),
                totalItemCount = totalItemCount
            ),
            selectedItemIndex = selectedItemIndex
        )
    }

    private fun sendViewItemListEvent(
        topicUi: TopicUi,
        title: String
    ) {
        val topicItems = listOf(
            topicUi.popularPages to POPULAR_PAGES_TITLE,
            topicUi.stepBySteps to STEP_BY_STEPS_TITLE,
            topicUi.services to SERVICES_TITLE,
        ).flatMap { (items, category) ->
            items.map { item ->
                EcommerceEvent.Item(
                    itemName = item.title,
                    itemCategory = category,
                    locationId = item.url
                )
            }
        }.toMutableList()

        topicUi.subtopics.forEach { subtopic ->
            topicItems += EcommerceEvent.Item(
                itemName = subtopic.title,
                itemCategory = BROWSE_TITLE,
                locationId = subtopic.ref
            )
        }

        analyticsClient.viewItemListEvent(
            ecommerceEvent = EcommerceEvent(
                itemListName = LIST_NAME,
                itemListId = title,
                items = topicItems,
                totalItemCount = topicItems.size
            )
        )
    }
}