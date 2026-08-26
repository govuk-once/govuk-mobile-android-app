package uk.gov.govuk.messages.ui

import androidx.lifecycle.SavedStateHandle
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
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
import uk.gov.govuk.messages.data.MessagesRepo
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.messages.MessagesDetailUiState
import uk.gov.govuk.messages.MessagesDetailViewModel
import uk.gov.govuk.messages.data.model.UpdateNotificationRequestBody
import uk.gov.govuk.messages.fixtures.MessagesFixtures.Companion.mockMessages
import uk.gov.govuk.messages.navigation.MESSAGES_DETAIL_ID_ARG
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class MessagesDetailViewModelTest {
    private val dispatcher = UnconfinedTestDispatcher()

    private val messagesRepo = mockk<MessagesRepo>(relaxed = true)
    private val analyticsClient = mockk<AnalyticsClient>(relaxed = true)
    private val savedStateHandle = mockk<SavedStateHandle>(relaxed = true)

    private lateinit var viewModel: MessagesDetailViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)

        viewModel = MessagesDetailViewModel(
            messagesRepo,
            analyticsClient,
            savedStateHandle
        )

        every { savedStateHandle.get<String>(MESSAGES_DETAIL_ID_ARG) } returns "1"

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
                    screenClass = "MessagesDetailScreen",
                    screenName = "MessagesDetail",
                    title = "MessagesDetailScreen"
                )
            }
        }
    }

    @Test
    fun `Given view created, then state is Default`() {
        runTest {
            val states = mutableListOf<MessagesDetailUiState>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.uiState.toList(states)
            }

            assertTrue(states[0] is MessagesDetailUiState.Default)
        }
    }

    @Test
    fun `Given view appears, then state transitions to Loading`() {
        runTest {
            coEvery { messagesRepo.getSingleMessage(any()) } coAnswers {
                delay(100)
                Result.Success(null)
            }

            val states = mutableListOf<MessagesDetailUiState>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.uiState.toList(states)
            }
            viewModel.onPageView()

            assertTrue(states.last() is MessagesDetailUiState.Loading)
        }
    }

    @Test
    fun `Given view appears, when offline, transitions to No Internet`() {
        runTest {
            coEvery { messagesRepo.getSingleMessage(any()) } coAnswers {
                delay(100)
                Result.DeviceOffline()
            }

            val states = mutableListOf<MessagesDetailUiState>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.uiState.toList(states)
            }
            viewModel.onPageView()

            advanceUntilIdle()

            assertTrue(states.last() is MessagesDetailUiState.NoInternet)
        }
    }
    @Test
    fun `Given view appears, when request fails, transitions to Error`() {
        runTest {
            coEvery { messagesRepo.getMessages() } coAnswers {
                delay(100)
                Result.Error()
            }

            val states = mutableListOf<MessagesDetailUiState>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.uiState.toList(states)
            }
            viewModel.onPageView()

            advanceUntilIdle()

            assertTrue(states.last() is MessagesDetailUiState.Error)
        }
    }

    @Test
    fun `Given view appears, when request returns result, transitions to Loaded`() {
        runTest {
            coEvery { messagesRepo.getSingleMessage("1") } coAnswers {
                delay(100)
                Result.Success(mockMessages.first())
            }

            val states = mutableListOf<MessagesDetailUiState>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.uiState.toList(states)
            }
            viewModel.onPageView()

            advanceUntilIdle()

            val lastState = states.last()

            assertTrue(lastState is MessagesDetailUiState.Loaded)

            val notification = (lastState as MessagesDetailUiState.Loaded).message

            assertEquals(notification, mockMessages.first())
        }
    }

    @Test
    fun `Given view appears, when request does not return result, transitions to Error`() {
        runTest {
            coEvery { messagesRepo.getSingleMessage("1") } coAnswers {
                delay(100)
                Result.Success(null)
            }

            val states = mutableListOf<MessagesDetailUiState>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.uiState.toList(states)
            }
            viewModel.onPageView()

            advanceUntilIdle()

            assertTrue(states.last() is MessagesDetailUiState.Error)
        }
    }

    @Test
    fun `Given request completes, and notification is read, does not update repo`() {
        runTest {
            coEvery { messagesRepo.getSingleMessage("1") } coAnswers {
                delay(100)
                Result.Success(mockMessages.first().copy(status = "READ"))
            }

            val states = mutableListOf<MessagesDetailUiState>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.uiState.toList(states)
            }
            viewModel.onPageView()

            advanceUntilIdle()


            coVerify(exactly = 0) {
                messagesRepo.updateMessage("1", UpdateNotificationRequestBody.Status.READ)
            }
        }
    }

    @Test
    fun `Given request completes, and notification is unread, updates repo`() {
        runTest {
            coEvery { messagesRepo.getSingleMessage("1") } coAnswers {
                delay(100)
                Result.Success(mockMessages.first())
            }

            val states = mutableListOf<MessagesDetailUiState>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.uiState.toList(states)
            }
            viewModel.onPageView()

            advanceUntilIdle()


            coVerify(exactly = 1) {
                messagesRepo.updateMessage("1", UpdateNotificationRequestBody.Status.READ)
            }
        }
    }

    // Link

    @Test
    fun `Given link tapped, then log analytics`() {
        val urlForTest = "Test"

        runTest {
            viewModel.onLinkTap(urlForTest)
            verify {
                analyticsClient.messagesUrlLaunched(urlForTest)
            }
        }
    }

    // Unread

    @Test
    fun `Given unread tapped, then log analytics`() {
        runTest {
            coEvery { messagesRepo.getSingleMessage("1") } coAnswers {
                delay(100)
                Result.Success(mockMessages.first())
            }

            viewModel.onPageView()

            advanceUntilIdle()

            viewModel.onTapMarkUnread()

            coVerify(exactly = 1) {
                messagesRepo.updateMessage("1", UpdateNotificationRequestBody.Status.READ)
            }
        }
    }

    @Test
    fun `Given unread tapped, then repo updated`() {
        runTest {
            coEvery { messagesRepo.getSingleMessage("1") } coAnswers {
                delay(100)
                Result.Success(mockMessages.first())
            }

            viewModel.onPageView()

            advanceUntilIdle()

            viewModel.onTapMarkUnread()
            verify {
                analyticsClient.messagesMarkUnread()
            }
        }
    }

    // Delete

    // TODO Check no-op when not loaded
    @Test
    fun `Given delete tapped, then log analytics`() {
        runTest {
            coEvery { messagesRepo.getSingleMessage("1") } coAnswers {
                delay(100)
                Result.Success(mockMessages.first())
            }

            viewModel.onPageView()

            advanceUntilIdle()

            viewModel.onTapDelete()
            verify {
                analyticsClient.messagesDelete()
            }
        }
    }

    @Test
    fun `Given delete confirmed, then log analytics`() {
        runTest {
            coEvery { messagesRepo.getSingleMessage("1") } coAnswers {
                delay(100)
                Result.Success(mockMessages.first())
            }

            viewModel.onPageView()

            advanceUntilIdle()

            viewModel.onConfirmDelete()
            verify {
                analyticsClient.messagesConfirmDelete()
            }
        }
    }

    @Test
    fun `Given delete cancelled, then log analytics`() {
        runTest {
            coEvery { messagesRepo.getSingleMessage("1") } coAnswers {
                delay(100)
                Result.Success(mockMessages.first())
            }

            viewModel.onPageView()

            advanceUntilIdle()

            viewModel.onCancelDelete()
            verify {
                analyticsClient.messagesCancelDelete()
            }
        }
    }
}