package uk.gov.govuk.messages.ui

import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.messages.MessagesUiState
import uk.gov.govuk.messages.MessagesViewModel
import uk.gov.govuk.messages.data.MessagesRepo
import uk.gov.govuk.messages.fixtures.MessagesFixtures.Companion.mockMessages

@OptIn(ExperimentalCoroutinesApi::class)
class MessagesViewModelTest {
    private val dispatcher = UnconfinedTestDispatcher()

    private val messagesRepo = mockk<MessagesRepo>(relaxed = true)
    private val analyticsClient = mockk<AnalyticsClient>(relaxed = true)

    private lateinit var viewModel: MessagesViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)

        viewModel = MessagesViewModel(
            messagesRepo,
            analyticsClient
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Given view appears, then log analytics`() {
        runTest {
            viewModel.onPageView()
            verify {
                analyticsClient.screenView(
                    screenClass = "MessagesScreen",
                    screenName = "Messages",
                    title = "MessagesScreen"
                )
            }
        }
    }

    @Test
    fun `Given view created, then state is Default`() {
        runTest {
            val states = mutableListOf<MessagesUiState>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.uiState.toList(states)
            }

            assertTrue(states[0] is MessagesUiState.Default)
        }
    }

    @Test
    fun `Given view appears, then state transitions to Loading`() {
        runTest {
            coEvery { messagesRepo.getMessages() } coAnswers {
                delay(100)
                Result.Success(listOf())
            }

            val states = mutableListOf<MessagesUiState>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.uiState.toList(states)
            }
            viewModel.onPageView()

            assertTrue(states.last() is MessagesUiState.Loading)
        }
    }

    @Test
    fun `Given view appears, when offline, transitions to No Internet`() {
        runTest {
            coEvery { messagesRepo.getMessages() } coAnswers {
                delay(100)
                Result.DeviceOffline()
            }

            val states = mutableListOf<MessagesUiState>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.uiState.toList(states)
            }
            viewModel.onPageView()

            advanceUntilIdle()

            assertTrue(states.last() is MessagesUiState.NoInternet)
        }
    }
    @Test
    fun `Given view appears, when request fails, transitions to Error`() {
        runTest {
            coEvery { messagesRepo.getMessages() } coAnswers {
                delay(100)
                Result.Error()
            }

            val states = mutableListOf<MessagesUiState>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.uiState.toList(states)
            }
            viewModel.onPageView()

            advanceUntilIdle()

            assertTrue(states.last() is MessagesUiState.Error)
        }
    }

    @Test
    fun `Given view appears, when request returns empty, transitions to Empty`() {
        runTest {
            coEvery { messagesRepo.getMessages() } coAnswers {
                delay(100)
                Result.Success(listOf())
            }

            val states = mutableListOf<MessagesUiState>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.uiState.toList(states)
            }
            viewModel.onPageView()

            advanceUntilIdle()

            assertTrue(states.last() is MessagesUiState.Empty)
        }
    }

    @Test
    fun `Given view appears, when request returns results, transitions to Loaded and filters into buckets`() {
        runTest {
            coEvery { messagesRepo.getMessages() } coAnswers {
                delay(100)
                Result.Success(mockMessages)
            }

            val states = mutableListOf<MessagesUiState>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.uiState.toList(states)
            }
            viewModel.onPageView()

            advanceUntilIdle()

            val lastState = states.last()

            assertTrue(lastState is MessagesUiState.Loaded)

            val notifications = (lastState as MessagesUiState.Loaded).notifications

            assertTrue(notifications.recent.size == 2)
            assertTrue(notifications.recent.contains(mockMessages[0]))
            assertTrue(notifications.recent.contains(mockMessages[1]))
            assertTrue(notifications.older.size == 1)
            assertTrue(notifications.older.contains(mockMessages[2]))

        }
    }
}