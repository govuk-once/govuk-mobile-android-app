package uk.gov.govuk.topics

import io.mockk.coVerify
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
import uk.gov.govuk.topics.data.TopicsRepo
import uk.gov.govuk.topics.domain.model.TopicItem
import uk.gov.govuk.topics.ui.model.TopicItemUi

@OptIn(ExperimentalCoroutinesApi::class)
class EditTopicsViewModelTest {

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
    fun `Given topics are emitted, When init, then emit topics`() {
        val topics = listOf(
            TopicItem(
                ref = "benefits",
                title = "Benefits",
                description = "description",
                isSelected = true
            )
        )

        every { topicsRepo.topics } returns flowOf(topics)

        val expected = listOf(
            TopicItemUi(
                ref = "benefits",
                icon = R.drawable.ic_topic_benefits,
                title = "Benefits",
                description = "description",
                isSelected = true
            )
        )

        val viewModel = EditTopicsViewModel(topicsRepo, analyticsClient)

        runTest {
            val result = viewModel.topics.first()
            assertEquals(expected, result)
        }
    }

    @Test
    fun `Given a user has selected a topic, When the topic is selected, then select topic in repo and send analytics`() {
        val viewModel = EditTopicsViewModel(topicsRepo, analyticsClient)

        viewModel.onTopicSelectedChanged("ref", "title", true)

        coVerify {
            topicsRepo.toggleSelection("ref", true)
            topicsRepo.topicsCustomised()
            analyticsClient.topicsCustomised()
            analyticsClient.toggleFunction(
                text = "title",
                section = "Topics",
                action = "Add"
            )
        }
    }

    @Test
    fun `Given a user has deselected a topic, When the topic is deselected, then deselect topic in repo and send analytics`() {
        val viewModel = EditTopicsViewModel(topicsRepo, analyticsClient)

        viewModel.onTopicSelectedChanged("ref", "title", false)

        coVerify {
            topicsRepo.toggleSelection("ref", false)
            topicsRepo.topicsCustomised()
            analyticsClient.topicsCustomised()
            analyticsClient.toggleFunction(
                text = "title",
                section = "Topics",
                action = "Remove"
            )
        }
    }

    @Test
    fun `Given a page view, then log analytics`() {
        val viewModel = EditTopicsViewModel(topicsRepo, analyticsClient)

        viewModel.onPageView("title")

        verify {
            analyticsClient.screenView(
                screenClass = "EditTopicsScreen",
                screenName = "Topic Selection",
                title = "title"
            )
        }
    }
}