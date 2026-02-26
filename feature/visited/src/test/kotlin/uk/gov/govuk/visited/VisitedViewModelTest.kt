package uk.gov.govuk.visited

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
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
import uk.gov.govuk.visited.data.VisitedRepo
import uk.gov.govuk.visited.data.localDateFormatter
import uk.gov.govuk.visited.data.store.VisitedLocalDataSource
import uk.gov.govuk.visited.domain.model.VisitedItemUi
import uk.gov.govuk.visited.ui.model.VisitedUi
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
class VisitedViewModelTest {
    private val dispatcher = UnconfinedTestDispatcher()
    private val visitedRepo = mockk<VisitedRepo>(relaxed = true)
    private val analyticsClient = mockk<AnalyticsClient>(relaxed = true)
    private val visitedLocalDataSource = mockk<VisitedLocalDataSource>(relaxed = true)
    private val visited = mockk<Visited>(relaxed = true)

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Given a page view, then log analytics`() {
        val viewModel =
            VisitedViewModel(visitedRepo, visitedLocalDataSource, visited, analyticsClient)

        viewModel.onPageView()

        verify {
            analyticsClient.screenView(
                screenClass = "VisitedScreen",
                screenName = "Pages you've visited",
                title = "Pages you've visited"
            )
        }
    }

    @Test
    fun `Given a visited item is removed, then log analytics`() {
        val viewModel =
            VisitedViewModel(visitedRepo, visitedLocalDataSource, visited, analyticsClient)

        viewModel.onRemoveVisitedItem("Title")

        verify {
            analyticsClient.buttonFunction(
                text = "Title",
                section = "Pages you've visited",
                action = "Remove"
            )
        }
    }

    @Test
    fun `Given remove all button click, then log analytics`() {
        val viewModel =
            VisitedViewModel(visitedRepo, visitedLocalDataSource, visited, analyticsClient)

        viewModel.onRemoveAllVisitedItems()

        verify {
            analyticsClient.buttonFunction(
                text = "",
                section = "Pages you've visited",
                action = "Remove all"
            )
        }
    }

    @Test
    fun `Given a done button click, then log analytics`() {
        val viewModel =
            VisitedViewModel(visitedRepo, visitedLocalDataSource, visited, analyticsClient)

        viewModel.onDoneClick()

        verify {
            analyticsClient.buttonFunction(
                text = "",
                section = "Pages you've visited",
                action = "Done"
            )
        }
    }

    @Test
    fun `Given the user re-views a visited item, then run the insert or update function`() {
        val viewModel =
            VisitedViewModel(visitedRepo, visitedLocalDataSource, visited, analyticsClient)

        runTest {
            viewModel.onVisitedItemClicked("visited item title", "visited item title")
            coVerify {
                visited.visitableItemClick("visited item title", "visited item title")
            }
        }
    }

    @Test
    fun `Given there are visited items, then the status in the view model is correct`() {
        mockkStatic(UUID::class)
        every { UUID.randomUUID() } returns UUID.fromString("5fc03087-d265-11e7-b8c6-83e29cd24f4d")

        val today = LocalDateTime.now()

        val visitedItems = listOf(
            VisitedItemUi(
                title = "GOV.UK",
                url = "https://www.gov.uk",
                lastVisited = today.toEpochSecond(ZoneOffset.UTC)
            ),
            VisitedItemUi(
                title = "Google",
                url = "https://www.google.com",
                lastVisited = today.toEpochSecond(ZoneOffset.UTC)
            )
        )

        val expected =
            VisitedUiState(
                visited = mapOf(
                    SectionTitles().today to listOf(
                        VisitedUi(
                            title = "GOV.UK",
                            url = "https://www.gov.uk",
                            lastVisited = localDateFormatter(today.toEpochSecond(ZoneOffset.UTC))
                        ),
                        VisitedUi(
                            title = "Google",
                            url = "https://www.google.com",
                            lastVisited = localDateFormatter(today.toEpochSecond(ZoneOffset.UTC))
                        )
                    )
                ),
                hasSelectedItems = false,
                hasAllSelectedItems = false
            )

        coEvery { visitedRepo.visitedItems } returns flowOf(visitedItems)

        val viewModel =
            VisitedViewModel(visitedRepo, visitedLocalDataSource, visited, analyticsClient)

        runTest {
            assertEquals(expected, viewModel.uiState.first())
        }
    }

    @Test
    fun `Given there are no visited items, then the status in the view model are correct`() {
        val visitedItems = emptyList<VisitedItemUi>()
        val expected = VisitedUiState(
            visited = emptyMap(),
            hasSelectedItems = false,
            hasAllSelectedItems = false
        )

        coEvery { visitedRepo.visitedItems } returns flowOf(visitedItems)

        val viewModel =
            VisitedViewModel(visitedRepo, visitedLocalDataSource, visited, analyticsClient)
        viewModel.onPageView()

        runTest {
            assertEquals(expected, viewModel.uiState.first())
        }
    }

    @Test
    fun `Given there are items, when onRemove is called, then the correct visited item is removed`() {
        val initialVisitedItems = listOf(
            VisitedItemUi("Title 1", "Url 1", 1024),
            VisitedItemUi("Title 2", "Url 2", 1024),
            VisitedItemUi("Title 3", "Url 3", 1024)
        )

        coEvery { visitedRepo.visitedItems } returns flowOf(initialVisitedItems)

        val viewModel =
            VisitedViewModel(visitedRepo, visitedLocalDataSource, visited, analyticsClient)

        runTest {
            viewModel.onVisitedItemRemoveClicked("Title 1", "Url 1")

            coVerify(exactly = 1) { visitedLocalDataSource.remove(any(), any()) }
            coVerify { visitedLocalDataSource.remove("Title 1", "Url 1") }
            coVerify { viewModel.onRemoveVisitedItem("Title 1") }
        }
    }

    @Test
    fun `Given there are items, when onRemoveAll is called, then the correct visited items are removed`() {
        val initialVisitedItems = listOf(
            VisitedItemUi("Title 1", "Url 1", 1024),
            VisitedItemUi("Title 2", "Url 2", 1024),
            VisitedItemUi("Title 3", "Url 3", 1024)
        )

        coEvery { visitedRepo.visitedItems } returns flowOf(initialVisitedItems)

        val viewModel =
            VisitedViewModel(visitedRepo, visitedLocalDataSource, visited, analyticsClient)

        runTest {
            viewModel.onRemoveAllVisitedItemsClicked()

            coVerify(exactly = 3) { visitedLocalDataSource.remove(any(), any()) }
            coVerify { visitedLocalDataSource.remove("Title 1", "Url 1") }
            coVerify { visitedLocalDataSource.remove("Title 2", "Url 2") }
            coVerify { visitedLocalDataSource.remove("Title 3", "Url 3") }
            coVerify { viewModel.onRemoveVisitedItem("Title 1") }
            coVerify { viewModel.onRemoveVisitedItem("Title 2") }
            coVerify { viewModel.onRemoveVisitedItem("Title 3") }
        }
    }
}
