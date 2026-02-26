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
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.analytics.data.local.model.EcommerceEvent
import uk.gov.govuk.topics.data.TopicsRepo
import uk.gov.govuk.topics.data.remote.model.RemoteTopic.RemoteTopicContent
import uk.gov.govuk.topics.navigation.TOPIC_REF_ARG
import uk.gov.govuk.topics.ui.model.TopicUi.TopicContent
import uk.gov.govuk.visited.Visited

@OptIn(ExperimentalCoroutinesApi::class)
class AllViewModelTest {

    companion object {
        private const val REF = "ref"
    }

    private val dispatcher = UnconfinedTestDispatcher()
    private val topicsRepo = mockk<TopicsRepo>(relaxed = true)
    private val analyticsClient = mockk<AnalyticsClient>(relaxed = true)
    private val savedStateHandle = mockk<SavedStateHandle>(relaxed = true)
    private val visited = mockk<Visited>(relaxed = true)

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        every { savedStateHandle.get<String>(TOPIC_REF_ARG) } returns REF
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Given a topic with popular pages, When init, then emit popular pages`() {
        coEvery { topicsRepo.popularPages } returns listOf(
            RemoteTopicContent(
                url = "url",
                title = "title",
                isStepByStep = false,
                isPopular = true
            )
        )

        val expected = listOf(
            TopicContent(
                url = "url",
                title = "title"
            )
        )

        val viewModel = AllViewModel(topicsRepo, analyticsClient, visited)

        runTest {
            val result = viewModel.popularPages.first()
            assertEquals(expected, result)
        }
    }

    @Test
    fun `Given a popular pages page view, then log analytics`() {
        val viewModel = AllViewModel(topicsRepo, analyticsClient, visited)

        viewModel.onPopularPagesView(
            title = "title",
            topicContentItems = listOf(
                TopicContent(
                    url = "url",
                    title = "title"
                )
            )
        )

        verify {
            analyticsClient.screenView(
                screenClass = "AllPopularPagesScreen",
                screenName = "title",
                title = "title"
            )
        }
    }

    @Test
    fun `Given a popular pages page view, then log ecommerce analytics`() {
        val viewModel = AllViewModel(topicsRepo, analyticsClient, visited)

        viewModel.onPopularPagesView(
            title = "title",
            topicContentItems = listOf(
                TopicContent(
                    url = "url1",
                    title = "title1"
                ),
                TopicContent(
                    url = "url2",
                    title = "title2"
                )
            )
        )

        verify {
            analyticsClient.viewItemListEvent(
                ecommerceEvent = EcommerceEvent(
                    itemListName = "Topics",
                    itemListId = "title",
                    items = listOf(
                        EcommerceEvent.Item(
                            itemName = "title1",
                            itemCategory = "Popular pages",
                            locationId = "url1",
                        ),
                        EcommerceEvent.Item(
                            itemName = "title2",
                            itemCategory = "Popular pages",
                            locationId = "url2",
                        )
                    ),
                    totalItemCount = 2
                )
            )
        }
    }

    @Test
    fun `Given a popular page click, then log analytics`() {
        val viewModel = AllViewModel(topicsRepo, analyticsClient, visited)

        viewModel.onPopularPagesClick(
            section = "section",
            text = "text",
            url = "url",
            selectedItemIndex = 1,
            popularPagesCount = 1
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
    fun `Given a popular page click, then log ecommerce analytics`() {
        val viewModel = AllViewModel(topicsRepo, analyticsClient, visited)

        viewModel.onPopularPagesClick(
            section = "section",
            text = "text",
            url = "url",
            selectedItemIndex = 1,
            popularPagesCount = 5
        )

        verify {
            analyticsClient.selectItemEvent(
                ecommerceEvent = EcommerceEvent(
                    itemListName = "Topics",
                    itemListId = "text",
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
    fun `Given a popular page click, then log visited item`() {
        val viewModel = AllViewModel(topicsRepo, analyticsClient, visited)

        viewModel.onPopularPagesClick(
            section = "section",
            text = "text",
            url = "url",
            selectedItemIndex = 1,
            popularPagesCount = 1
        )

        coVerify {
            visited.visitableItemClick(title = "text", url = "url")
        }
    }

    @Test
    fun `Given a topic with step by steps, When init, then emit step by steps`() {
        coEvery { topicsRepo.stepBySteps } returns listOf(
            RemoteTopicContent(
                url = "url",
                title = "title",
                isStepByStep = true,
                isPopular = false
            )
        )

        val expected = listOf(
            TopicContent(
                url = "url",
                title = "title"
            )
        )

        val viewModel = AllViewModel(topicsRepo, analyticsClient, visited)

        runTest {
            val result = viewModel.stepBySteps.first()
            assertEquals(expected, result)
        }
    }

    @Test
    fun `Given a step by steps page view, then log analytics`() {
        val viewModel = AllViewModel(topicsRepo, analyticsClient, visited)

        viewModel.onStepByStepPageView(
            title = "title",
            topicContentItems = listOf(
                TopicContent(
                    url = "url",
                    title = "title"
                )
            )
        )

        verify {
            analyticsClient.screenView(
                screenClass = "AllStepByStepsScreen",
                screenName = "title",
                title = "title"
            )
        }
    }

    @Test
    fun `Given a step by steps page view, then log ecommerce analytics`() {
        val viewModel = AllViewModel(topicsRepo, analyticsClient, visited)

        viewModel.onStepByStepPageView(
            title = "title",
            topicContentItems = listOf(
                TopicContent(
                    url = "url1",
                    title = "title1"
                ),
                TopicContent(
                    url = "url2",
                    title = "title2"
                )
            )
        )

        verify {
            analyticsClient.viewItemListEvent(
                ecommerceEvent = EcommerceEvent(
                    itemListName = "Topics",
                    itemListId = "title",
                    items = listOf(
                        EcommerceEvent.Item(
                            itemName = "title1",
                            itemCategory = "Step by step guides",
                            locationId = "url1"
                        ),
                        EcommerceEvent.Item(
                            itemName = "title2",
                            itemCategory = "Step by step guides",
                            locationId = "url2"
                        )
                    ),
                    totalItemCount = 2
                )
            )
        }
    }

    @Test
    fun `Given a step by step click, then log analytics`() {
        val viewModel = AllViewModel(topicsRepo, analyticsClient, visited)

        viewModel.onStepByStepClick(
            section = "section",
            text = "text",
            url = "url",
            selectedItemIndex = 1,
            stepByStepsCount = 1
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
    fun `Given a step by step click, then log ecommerce analytics`() {
        val viewModel = AllViewModel(topicsRepo, analyticsClient, visited)

        viewModel.onStepByStepClick(
            section = "section",
            text = "text",
            url = "url",
            selectedItemIndex = 1,
            stepByStepsCount = 5
        )

        verify {
            analyticsClient.selectItemEvent(
                ecommerceEvent = EcommerceEvent(
                    itemListName = "Topics",
                    itemListId = "text",
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
    fun `Given a step by step click, then log visited item`() {
        val viewModel = AllViewModel(topicsRepo, analyticsClient, visited)

        viewModel.onStepByStepClick(
            section = "section",
            text = "text",
            url = "url",
            selectedItemIndex = 1,
            stepByStepsCount = 1
        )

        coVerify {
            visited.visitableItemClick(title = "text", url = "url")
        }
    }
}
