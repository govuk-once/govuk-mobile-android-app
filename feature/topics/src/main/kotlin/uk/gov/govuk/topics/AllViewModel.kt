package uk.gov.govuk.topics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.analytics.data.local.model.EcommerceEvent
import uk.gov.govuk.topics.data.TopicsRepo
import uk.gov.govuk.topics.extension.toTopicContent
import uk.gov.govuk.topics.ui.model.TopicUi.TopicContent
import uk.gov.govuk.visited.Visited
import javax.inject.Inject

@HiltViewModel
internal class AllViewModel @Inject constructor(
    topicsRepo: TopicsRepo,
    private val analyticsClient: AnalyticsClient,
    private val visited: Visited
): ViewModel() {

    companion object {
        private const val POPULAR_SCREEN_CLASS = "AllPopularPagesScreen"
        private const val POPULAR_SCREEN_TITLE = "Popular pages"
        private const val STEP_BY_STEP_SCREEN_CLASS = "AllStepByStepsScreen"
        private const val STEP_BY_STEP_SCREEN_TITLE = "Step by step guides"
        private const val LIST_NAME = "Topics"
    }

    private val _stepBySteps: MutableStateFlow<List<TopicContent>> = MutableStateFlow(
        topicsRepo.stepBySteps.map { it.toTopicContent() }
    )
    val stepBySteps = _stepBySteps.asStateFlow()

    private val _popularPages: MutableStateFlow<List<TopicContent>> = MutableStateFlow(
        topicsRepo.popularPages.map { it.toTopicContent() }
    )
    val popularPages = _popularPages.asStateFlow()

    fun onPopularPagesView(
        topicContentItems: List<TopicContent>,
        title: String
    ) {
        onPageView(
            topicContentItems = topicContentItems,
            title = title,
            screenClass = POPULAR_SCREEN_CLASS,
            screenTitle = POPULAR_SCREEN_TITLE
        )
    }

    fun onStepByStepPageView(
        topicContentItems: List<TopicContent>,
        title: String
    ) {
        onPageView(
            topicContentItems = topicContentItems,
            title = title,
            screenClass = STEP_BY_STEP_SCREEN_CLASS,
            screenTitle = STEP_BY_STEP_SCREEN_TITLE
        )
    }

    fun onPopularPagesClick(
        section: String,
        text: String,
        url: String,
        selectedItemIndex: Int,
        popularPagesCount: Int
    ) {
        onClick(
            analyticsClient = analyticsClient,
            viewModelScope = viewModelScope,
            visited = visited,
            section = section,
            text = text,
            url = url,
            selectedItemIndex = selectedItemIndex,
            itemCount = popularPagesCount
        )
    }

    fun onStepByStepClick(
        section: String,
        text: String,
        url: String,
        selectedItemIndex: Int,
        stepByStepsCount: Int
    ) {
        onClick(
            analyticsClient = analyticsClient,
            viewModelScope = viewModelScope,
            visited = visited,
            section = section,
            text = text,
            url = url,
            selectedItemIndex = selectedItemIndex,
            itemCount = stepByStepsCount
        )
    }

    private fun onPageView(
        topicContentItems: List<TopicContent>,
        title: String,
        screenClass: String,
        screenTitle: String
    ) {
        analyticsClient.screenView(
            screenClass = screenClass,
            screenName = title,
            title = title
        )

        sendViewItemListEvent(
            topicContentItems = topicContentItems,
            title = title,
            screenTitle = screenTitle
        )
    }

    private fun sendViewItemListEvent(
        topicContentItems: List<TopicContent>,
        title: String,
        screenTitle: String
    ) {
        var topicItems = listOf<EcommerceEvent.Item>()

        if (topicContentItems.isNotEmpty()) {
            topicItems = listOf(
                topicContentItems to screenTitle,
            ).flatMap { (items, category) ->
                items.map { item ->
                    EcommerceEvent.Item(
                        itemName = item.title,
                        itemCategory = category,
                        locationId = item.url
                    )
                }
            }
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

    private fun onClick(
        analyticsClient: AnalyticsClient,
        viewModelScope: CoroutineScope,
        visited: Visited,
        section: String,
        text: String,
        url: String,
        selectedItemIndex: Int,
        itemCount: Int
    ) {
        analyticsClient.buttonClick(
            text = text,
            url = url,
            external = true,
            section = section
        )

        sendSelectItemEvent(
            analyticsClient = analyticsClient,
            title = text,
            section = section,
            text = text,
            url = url,
            selectedItemIndex = selectedItemIndex,
            itemCount = itemCount
        )

        viewModelScope.launch {
            visited.visitableItemClick(title = text, url = url)
        }
    }

    private fun sendSelectItemEvent(
        analyticsClient: AnalyticsClient,
        section: String,
        text: String,
        title: String?,
        url: String?,
        selectedItemIndex: Int,
        itemCount: Int
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
                totalItemCount = itemCount
            ),
            selectedItemIndex = selectedItemIndex
        )
    }
}
