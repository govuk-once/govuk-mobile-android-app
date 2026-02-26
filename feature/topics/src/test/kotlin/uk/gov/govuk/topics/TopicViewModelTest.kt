package uk.gov.govuk.topics

import androidx.lifecycle.SavedStateHandle
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.analytics.data.local.model.EcommerceEvent
import uk.gov.govuk.data.model.Result.DeviceOffline
import uk.gov.govuk.data.model.Result.Error
import uk.gov.govuk.data.model.Result.ServiceNotResponding
import uk.gov.govuk.data.model.Result.Success
import uk.gov.govuk.topics.data.TopicsRepo
import uk.gov.govuk.topics.data.remote.model.RemoteTopic
import uk.gov.govuk.topics.navigation.TOPIC_REF_ARG
import uk.gov.govuk.topics.navigation.TOPIC_SUBTOPIC_ARG
import uk.gov.govuk.topics.ui.model.TopicUi
import uk.gov.govuk.visited.Visited

@OptIn(ExperimentalCoroutinesApi::class)
class TopicViewModelTest {

    companion object {
        private const val REF = "ref"
    }

    private val dispatcher = UnconfinedTestDispatcher()
    private val topicsRepo = mockk<TopicsRepo>(relaxed = true)
    private val analyticsClient = mockk<AnalyticsClient>(relaxed = true)
    private val savedStateHandle = mockk<SavedStateHandle>(relaxed = true)
    private val visited = mockk<Visited>(relaxed = true)

    private val remoteTopic = RemoteTopic(
        title = "title",
        description = "description",
        subtopics = emptyList(),
        content = emptyList()
    )

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        every { savedStateHandle.get<String>(TOPIC_REF_ARG) } returns REF
        every { savedStateHandle.get<Boolean>(TOPIC_SUBTOPIC_ARG) } returns false
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Given a topic is returned, When init, then emit topic`() {
        coEvery { topicsRepo.getTopic(REF) } returns Success(remoteTopic)

        val expected = TopicUi(
            title = "title",
            description = "description",
            popularPages = emptyList(),
            displayPopularPagesSeeAll = false,
            stepBySteps = emptyList(),
            displayStepByStepSeeAll = false,
            services = emptyList(),
            subtopics = emptyList(),
            subtopicsSection = TopicUi.Section(
                title = R.string.browse_title,
                icon = R.drawable.ic_topic_browse
            )
        )

        val viewModel = TopicViewModel(topicsRepo, analyticsClient, visited, savedStateHandle)

        runTest {
            val result = viewModel.uiState.first() as TopicUiState.Default
            assertEquals(expected, result.topicUi)
        }
    }

    @Test
    fun `Given a topic is returned, When init, then the results and status in the view model are correct`() {
        coEvery { topicsRepo.getTopic(REF) } returns Success(remoteTopic)

        val viewModel = TopicViewModel(topicsRepo, analyticsClient, visited, savedStateHandle)

        runTest {
            val result = viewModel.uiState.first()
            assertTrue(result is TopicUiState.Default)
        }
    }

    @Test
    fun `Given the device is offline, When init, then the results and status in the view model are correct`() {
        coEvery { topicsRepo.getTopic(REF) } returns DeviceOffline()

        val viewModel = TopicViewModel(topicsRepo, analyticsClient, visited, savedStateHandle)

        runTest {
            val result = viewModel.uiState.first() as TopicUiState.Offline
            assertEquals(REF, result.topicReference)
        }
    }

    @Test
    fun `Given the topic API is unavailable, When init, then the results and status in the view model are correct`() {
        coEvery { topicsRepo.getTopic(REF) } returns ServiceNotResponding()

        val viewModel = TopicViewModel(topicsRepo, analyticsClient, visited, savedStateHandle)

        runTest {
            val result = viewModel.uiState.first() as TopicUiState.ServiceError
            assertEquals(REF, result.topicReference)
        }
    }

    @Test
    fun `Given there is a general error when getting a topic, When init, then the results and status in the view model are correct`() {
        coEvery { topicsRepo.getTopic(REF) } returns Error()

        val viewModel = TopicViewModel(topicsRepo, analyticsClient, visited, savedStateHandle)

        runTest {
            val result = viewModel.uiState.first() as TopicUiState.ServiceError
            assertEquals(REF, result.topicReference)
        }
    }

    @Test
    fun `Given a page view, then log analytics`() {
        coEvery { topicsRepo.getTopic(REF) } returns Success(remoteTopic)

        val viewModel = TopicViewModel(topicsRepo, analyticsClient, visited, savedStateHandle)

        viewModel.onPageView(title = "title")

        verify {
            analyticsClient.screenView(
                screenClass = "TopicScreen",
                screenName = "title",
                title = "title"
            )
        }
    }

    @Test
    fun `Given an error page view, then don't log ecommerce analytics`() {
        coEvery { topicsRepo.getTopic(REF) } returns Success(remoteTopic)

        val viewModel = TopicViewModel(topicsRepo, analyticsClient, visited, savedStateHandle)

        viewModel.onPageView(title = "title")

        verify(exactly = 0) {
            analyticsClient.viewItemListEvent(any())
        }
    }

    @Test
    fun `Given a page view, then log ecommerce analytics`() {
        coEvery { topicsRepo.getTopic(REF) } returns Success(remoteTopic)

        val viewModel = TopicViewModel(topicsRepo, analyticsClient, visited, savedStateHandle)
        val popularPagesSection = listOf(
            TopicUi.TopicContent(
                title = "title1",
                url = "url1"
            ),
            TopicUi.TopicContent(
                title = "title2",
                url = "url2"
            )
        )
        val topicUi = TopicUi(
            title = "title",
            description = "description",
            popularPages = popularPagesSection,
            displayPopularPagesSeeAll = true,
            stepBySteps = emptyList(),
            displayStepByStepSeeAll = false,
            services = emptyList(),
            subtopics = emptyList(),
            subtopicsSection = TopicUi.Section(
                title = R.string.browse_title,
                icon = R.drawable.ic_topic_browse
            )
        )

        viewModel.onPageView(
            topicUi = topicUi,
            title = "title"
        )

        verify {
            analyticsClient.viewItemListEvent(
                ecommerceEvent = EcommerceEvent(
                    itemListName = "Topics",
                    itemListId = "title",
                    items = popularPagesSection.map {
                        EcommerceEvent.Item(
                            itemName = it.title,
                            itemCategory = "Popular pages",
                            locationId = it.url
                        )
                    },
                    totalItemCount = 2
                )
            )
        }
    }

    @Test
    fun `Given a content click, then log analytics`() {
        coEvery { topicsRepo.getTopic(REF) } returns Success(remoteTopic)

        val viewModel = TopicViewModel(topicsRepo, analyticsClient, visited, savedStateHandle)

        viewModel.onContentClick(
            section = "section",
            text = "text",
            url = "url",
            title = "title",
            selectedItemIndex = 1,
            totalItemCount = 1
        )

        verify {
            analyticsClient.buttonClick(
                text = "text",
                url = "url",
                external = true,
                section = "section"
            )
        }
    }

    @Test
    fun `Given a content click, then log ecommerce analytics`() {
        coEvery { topicsRepo.getTopic(REF) } returns Success(remoteTopic)

        val viewModel = TopicViewModel(topicsRepo, analyticsClient, visited, savedStateHandle)

        viewModel.onContentClick(
            section = "section",
            text = "text",
            url = "url",
            title = "title",
            selectedItemIndex = 1,
            totalItemCount = 5
        )

        verify {
            analyticsClient.selectItemEvent(
                ecommerceEvent = EcommerceEvent(
                    itemListName = "Topics",
                    itemListId = "title",
                    items = listOf(
                        EcommerceEvent.Item(
                            itemName = "text",
                            itemCategory = "section",
                            locationId = "url"
                        )
                    ),
                    totalItemCount = 5
                ),
                selectedItemIndex = 1
            )
        }
    }

    @Test
    fun `Given a content click, then log visited item`() {
        coEvery { topicsRepo.getTopic(REF) } returns Success(remoteTopic)

        val viewModel = TopicViewModel(topicsRepo, analyticsClient, visited, savedStateHandle)

        viewModel.onContentClick(
            section = "section",
            text = "text",
            url = "url",
            title = "title",
            selectedItemIndex = 1,
            totalItemCount = 5
        )

        coVerify {
            visited.visitableItemClick(title = "text", url = "url")
        }
    }

    @Test
    fun `Given a see all click, then log analytics`() {
        coEvery { topicsRepo.getTopic(REF) } returns Success(remoteTopic)

        val viewModel = TopicViewModel(topicsRepo, analyticsClient, visited, savedStateHandle)

        viewModel.onSeeAllClick(
            section = "section",
            text = "text"
        )

        verify {
            analyticsClient.buttonClick(
                text = "text",
                external = false,
                section = "section"
            )
        }
    }

    @Test
    fun `Given a subtopic click, then log analytics`() {
        coEvery { topicsRepo.getTopic(REF) } returns Success(remoteTopic)

        val viewModel = TopicViewModel(topicsRepo, analyticsClient, visited, savedStateHandle)

        viewModel.onSubtopicClick("text", 1, 1)

        verify {
            analyticsClient.buttonClick(
                text = "text",
                external = false,
                section = "Sub topics"
            )
        }
    }

    @Test
    fun `Given a subtopic click, then log ecommerce analytics`() {
        coEvery { topicsRepo.getTopic(REF) } returns Success(remoteTopic)

        val viewModel = TopicViewModel(topicsRepo, analyticsClient, visited, savedStateHandle)

        viewModel.onSubtopicClick("text", 1, 5)

        verify {
            analyticsClient.selectItemEvent(
                ecommerceEvent = EcommerceEvent(
                    itemListName = "Topics",
                    itemListId = "",
                    items = listOf(
                        EcommerceEvent.Item(
                            itemName = "text",
                            itemCategory = "Sub topics",
                            locationId = ""
                        )
                    ),
                    totalItemCount = 5
                ),
                selectedItemIndex = 1
            )
        }
    }
}
