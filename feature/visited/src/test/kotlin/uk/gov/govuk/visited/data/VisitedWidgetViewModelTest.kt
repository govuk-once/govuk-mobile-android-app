package uk.gov.govuk.visited.data

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
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
import uk.gov.govuk.visited.Visited
import uk.gov.govuk.visited.VisitedWidgetUiState
import uk.gov.govuk.visited.VisitedWidgetViewModel
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
    fun `Given the user re-views a visited item, then run the insert or update function`() {
        val viewModel =
            VisitedWidgetViewModel(visitedRepo, visited, analyticsClient)

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
            ),
            VisitedItemUi(
                title = "Android",
                url = "https://www.android.com",
                lastVisited = today.toEpochSecond(ZoneOffset.UTC)
            ),
            VisitedItemUi(
                title = "Apple",
                url = "https://www.apple.com",
                lastVisited = today.toEpochSecond(ZoneOffset.UTC)
            )
        )

        val expected =
            VisitedWidgetUiState.Visited(
                listOf(
                    VisitedUi(
                        title = "GOV.UK",
                        url = "https://www.gov.uk",
                        lastVisited = localDateFormatter(today.toEpochSecond(ZoneOffset.UTC))
                    ),
                    VisitedUi(
                        title = "Google",
                        url = "https://www.google.com",
                        lastVisited = localDateFormatter(today.toEpochSecond(ZoneOffset.UTC))
                    ),
                    VisitedUi(
                        title = "Android",
                        url = "https://www.android.com",
                        lastVisited = localDateFormatter(today.toEpochSecond(ZoneOffset.UTC))
                    )
                )
            )

        coEvery { visitedRepo.visitedItems } returns flowOf(visitedItems)

        val viewModel =
            VisitedWidgetViewModel(visitedRepo, visited, analyticsClient)

        runTest {
            assertEquals(expected, viewModel.uiState.first())
        }
    }

    @Test
    fun `Given there are no visited items, then the status in the view model are correct`() {
        val visitedItems = emptyList<VisitedItemUi>()
        val expected = VisitedWidgetUiState.NoVisited

        coEvery { visitedRepo.visitedItems } returns flowOf(visitedItems)

        val viewModel =
            VisitedWidgetViewModel(visitedRepo, visited, analyticsClient)

        runTest {
            assertEquals(expected, viewModel.uiState.first())
        }
    }
}