package uk.gov.govuk.topics

import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.analytics.data.local.model.EcommerceEvent
import uk.gov.govuk.topics.data.TopicsRepo
import uk.gov.govuk.topics.domain.model.TopicItem
import uk.gov.govuk.topics.ui.model.TopicItemUi

@OptIn(ExperimentalCoroutinesApi::class)
class TopicsWidgetViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private val topicsRepo = mockk<TopicsRepo>(relaxed = true)
    private val analyticsClient = mockk<AnalyticsClient>(relaxed = true)

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Given topics are emitted, When init, then emit ui state`() {
        val topics = listOf(
            TopicItem(
                ref = "benefits",
                title = "Benefits",
                description = "description",
                isSelected = true
            ),
            TopicItem(
                ref = "care",
                title = "Care",
                description = "description",
                isSelected = false
            )
        )

        every { topicsRepo.topics } returns flowOf(topics)
        coEvery { topicsRepo.isTopicsCustomised() } returns false

        val expected =
            TopicsWidgetUiState(
                yourTopics = listOf(
                    TopicItemUi(
                        ref = "benefits",
                        icon = R.drawable.ic_topic_benefits,
                        title = "Benefits",
                        description = "description",
                        isSelected = true
                    )
                ),
                allTopics = listOf(
                    TopicItemUi(
                        ref = "benefits",
                        icon = R.drawable.ic_topic_benefits,
                        title = "Benefits",
                        description = "description",
                        isSelected = true
                    ),
                    TopicItemUi(
                        ref = "care",
                        icon = R.drawable.ic_topic_care,
                        title = "Care",
                        description = "description",
                        isSelected = false
                    )
                )
            )

        val viewModel = TopicsWidgetViewModel(topicsRepo, analyticsClient)

        runTest {
            assertEquals(expected, viewModel.uiState.first())
        }
    }

    @Test
    fun `Given your topics are viewed, Then send a view item list event`() {
        val viewModel = TopicsWidgetViewModel(topicsRepo, analyticsClient)

        val topics = listOf(
            TopicItemUi(
                ref = "benefits",
                title = "Benefits",
                description = "description",
                isSelected = true,
                icon = R.drawable.ic_topic_benefits
            ),
            TopicItemUi(
                ref = "care",
                title = "Care",
                description = "description",
                isSelected = true,
                icon = R.drawable.ic_topic_care
            )
        )

        viewModel.onView(TopicsCategory.YOUR, topics)

        verify {
            analyticsClient.viewItemListEvent(
                ecommerceEvent = EcommerceEvent(
                    itemListName = "Your topics",
                    itemListId = "Your topics",
                    items = listOf(
                        EcommerceEvent.Item(
                            itemName = "Benefits",
                            locationId = "benefits"
                        ),
                        EcommerceEvent.Item(
                            itemName = "Care",
                            locationId = "care"
                        )
                    ),
                    totalItemCount = 2
                )
            )
        }
    }

    @Test
    fun `Given all topics are viewed, Then send a view item list event`() {
        val viewModel = TopicsWidgetViewModel(topicsRepo, analyticsClient)

        val topics = listOf(
            TopicItemUi(
                ref = "benefits",
                title = "Benefits",
                description = "description",
                isSelected = true,
                icon = R.drawable.ic_topic_benefits
            ),
            TopicItemUi(
                ref = "care",
                title = "Care",
                description = "description",
                isSelected = true,
                icon = R.drawable.ic_topic_care
            )
        )

        viewModel.onView(TopicsCategory.ALL, topics)

        verify {
            analyticsClient.viewItemListEvent(
                ecommerceEvent = EcommerceEvent(
                    itemListName = "All topics",
                    itemListId = "All topics",
                    items = listOf(
                        EcommerceEvent.Item(
                            itemName = "Benefits",
                            locationId = "benefits"
                        ),
                        EcommerceEvent.Item(
                            itemName = "Care",
                            locationId = "care"
                        )
                    ),
                    totalItemCount = 2
                )
            )
        }
    }

    @Test
    fun `Given empty topics are viewed, Then send a view item list event`() {
        val viewModel = TopicsWidgetViewModel(topicsRepo, analyticsClient)

        viewModel.onView(TopicsCategory.YOUR, emptyList())

        verify {
            analyticsClient.viewItemListEvent(
                ecommerceEvent = EcommerceEvent(
                    itemListName = "Your topics",
                    itemListId = "Your topics",
                    items = emptyList(),
                    totalItemCount = 0
                )
            )
        }
    }

    @Test
    fun `Given a your topics widget item is clicked, then send a select item event`() {
        val viewModel = TopicsWidgetViewModel(topicsRepo, analyticsClient)

        viewModel.onTopicSelectClick(
            category = TopicsCategory.YOUR,
            title = "Benefits",
            ref = "benefits",
            selectedItemIndex = 1,
            topicCount = 5
        )

        verify {
            analyticsClient.selectItemEvent(
                ecommerceEvent = EcommerceEvent(
                    itemListName = "Your topics",
                    itemListId = "Your topics",
                    items = listOf(
                        EcommerceEvent.Item(
                            itemName = "Benefits",
                            locationId = "benefits"
                        )
                    ),
                    totalItemCount = 5
                ),
                selectedItemIndex = 1
            )
        }
    }

    @Test
    fun `Given an all topics widget item is clicked, then send a select item event`() {
        val viewModel = TopicsWidgetViewModel(topicsRepo, analyticsClient)

        viewModel.onTopicSelectClick(
            category = TopicsCategory.ALL,
            title = "Benefits",
            ref = "benefits",
            selectedItemIndex = 1,
            topicCount = 5
        )

        verify {
            analyticsClient.selectItemEvent(
                ecommerceEvent = EcommerceEvent(
                    itemListName = "All topics",
                    itemListId = "All topics",
                    items = listOf(
                        EcommerceEvent.Item(
                            itemName = "Benefits",
                            locationId = "benefits"
                        )
                    ),
                    totalItemCount = 5
                ),
                selectedItemIndex = 1
            )
        }
    }
}
