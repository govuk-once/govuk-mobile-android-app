package uk.gov.govuk.notificationcentre.ui

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
import uk.gov.govuk.notificationcentre.data.NotificationCentreRepo
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.notificationcentre.NotificationCentreDetailUiState
import uk.gov.govuk.notificationcentre.NotificationCentreDetailViewModel
import uk.gov.govuk.notificationcentre.data.model.UpdateNotificationRequestBody
import uk.gov.govuk.notificationcentre.fixtures.NotificationCentreFixtures.Companion.mockNotifications
import uk.gov.govuk.notificationcentre.navigation.NOTIFICATION_CENTRE_DETAIL_ID_ARG
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class NotificationCentreDetailViewModelTest {
    private val dispatcher = UnconfinedTestDispatcher()

    private val notificationCentreRepo = mockk<NotificationCentreRepo>(relaxed = true)
    private val analyticsClient = mockk<AnalyticsClient>(relaxed = true)
    private val savedStateHandle = mockk<SavedStateHandle>(relaxed = true)

    private lateinit var viewModel: NotificationCentreDetailViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)

        viewModel = NotificationCentreDetailViewModel(
            notificationCentreRepo,
            analyticsClient,
            savedStateHandle
        )

        every { savedStateHandle.get<String>(NOTIFICATION_CENTRE_DETAIL_ID_ARG) } returns "1"

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
                    screenClass = "NotificationCentreDetailScreen",
                    screenName = "NotificationCentreDetail",
                    title = "NotificationCentreDetailScreen"
                )
            }
        }
    }

    @Test
    fun `Given view created, then state is Default`() {
        runTest {
            val states = mutableListOf<NotificationCentreDetailUiState>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.uiState.toList(states)
            }

            assertTrue(states[0] is NotificationCentreDetailUiState.Default)
        }
    }

    @Test
    fun `Given view appears, then state transitions to Loading`() {
        runTest {
            coEvery { notificationCentreRepo.getSingleNotification(any()) } coAnswers {
                delay(100)
                Result.Success(null)
            }

            val states = mutableListOf<NotificationCentreDetailUiState>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.uiState.toList(states)
            }
            viewModel.onPageView()

            assertTrue(states.last() is NotificationCentreDetailUiState.Loading)
        }
    }

    @Test
    fun `Given view appears, when offline, transitions to No Internet`() {
        runTest {
            coEvery { notificationCentreRepo.getSingleNotification(any()) } coAnswers {
                delay(100)
                Result.DeviceOffline()
            }

            val states = mutableListOf<NotificationCentreDetailUiState>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.uiState.toList(states)
            }
            viewModel.onPageView()

            advanceUntilIdle()

            assertTrue(states.last() is NotificationCentreDetailUiState.NoInternet)
        }
    }
    @Test
    fun `Given view appears, when request fails, transitions to Error`() {
        runTest {
            coEvery { notificationCentreRepo.getNotifications() } coAnswers {
                delay(100)
                Result.Error()
            }

            val states = mutableListOf<NotificationCentreDetailUiState>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.uiState.toList(states)
            }
            viewModel.onPageView()

            advanceUntilIdle()

            assertTrue(states.last() is NotificationCentreDetailUiState.Error)
        }
    }

    @Test
    fun `Given view appears, when request returns result, transitions to Loaded`() {
        runTest {
            coEvery { notificationCentreRepo.getSingleNotification("1") } coAnswers {
                delay(100)
                Result.Success(mockNotifications.first())
            }

            val states = mutableListOf<NotificationCentreDetailUiState>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.uiState.toList(states)
            }
            viewModel.onPageView()

            advanceUntilIdle()

            val lastState = states.last()

            assertTrue(lastState is NotificationCentreDetailUiState.Loaded)

            val notification = (lastState as NotificationCentreDetailUiState.Loaded).notification

            assertEquals(notification, mockNotifications.first())
        }
    }

    @Test
    fun `Given view appears, when request does not return result, transitions to Error`() {
        runTest {
            coEvery { notificationCentreRepo.getSingleNotification("1") } coAnswers {
                delay(100)
                Result.Success(null)
            }

            val states = mutableListOf<NotificationCentreDetailUiState>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.uiState.toList(states)
            }
            viewModel.onPageView()

            advanceUntilIdle()

            assertTrue(states.last() is NotificationCentreDetailUiState.Error)
        }
    }

    @Test
    fun `Given request completes, and notification is read, does not update repo`() {
        runTest {
            coEvery { notificationCentreRepo.getSingleNotification("1") } coAnswers {
                delay(100)
                Result.Success(mockNotifications.first().copy(status = "READ"))
            }

            val states = mutableListOf<NotificationCentreDetailUiState>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.uiState.toList(states)
            }
            viewModel.onPageView()

            advanceUntilIdle()


            coVerify(exactly = 0) {
                notificationCentreRepo.updateNotification("1", UpdateNotificationRequestBody.Status.READ)
            }
        }
    }

    @Test
    fun `Given request completes, and notification is unread, updates repo`() {
        runTest {
            coEvery { notificationCentreRepo.getSingleNotification("1") } coAnswers {
                delay(100)
                Result.Success(mockNotifications.first())
            }

            val states = mutableListOf<NotificationCentreDetailUiState>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.uiState.toList(states)
            }
            viewModel.onPageView()

            advanceUntilIdle()


            coVerify(exactly = 1) {
                notificationCentreRepo.updateNotification("1", UpdateNotificationRequestBody.Status.READ)
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
                analyticsClient.notificationCentreUrlLaunched(urlForTest)
            }
        }
    }

    // Unread

    @Test
    fun `Given unread tapped, then log analytics`() {
        runTest {
            coEvery { notificationCentreRepo.getSingleNotification("1") } coAnswers {
                delay(100)
                Result.Success(mockNotifications.first())
            }

            viewModel.onPageView()

            advanceUntilIdle()

            viewModel.onTapMarkUnread()

            coVerify(exactly = 1) {
                notificationCentreRepo.updateNotification("1", UpdateNotificationRequestBody.Status.READ)
            }
        }
    }

    @Test
    fun `Given unread tapped, then repo updated`() {
        runTest {
            coEvery { notificationCentreRepo.getSingleNotification("1") } coAnswers {
                delay(100)
                Result.Success(mockNotifications.first())
            }

            viewModel.onPageView()

            advanceUntilIdle()

            viewModel.onTapMarkUnread()
            verify {
                analyticsClient.notificationCentreMarkUnread()
            }
        }
    }

    // Delete

    // TODO Check no-op when not loaded
    @Test
    fun `Given delete tapped, then log analytics`() {
        runTest {
            coEvery { notificationCentreRepo.getSingleNotification("1") } coAnswers {
                delay(100)
                Result.Success(mockNotifications.first())
            }

            viewModel.onPageView()

            advanceUntilIdle()

            viewModel.onTapDelete()
            verify {
                analyticsClient.notificationCentreDelete()
            }
        }
    }

    @Test
    fun `Given delete confirmed, then log analytics`() {
        runTest {
            coEvery { notificationCentreRepo.getSingleNotification("1") } coAnswers {
                delay(100)
                Result.Success(mockNotifications.first())
            }

            viewModel.onPageView()

            advanceUntilIdle()

            viewModel.onConfirmDelete()
            verify {
                analyticsClient.notificationCentreConfirmDelete()
            }
        }
    }

    @Test
    fun `Given delete cancelled, then log analytics`() {
        runTest {
            coEvery { notificationCentreRepo.getSingleNotification("1") } coAnswers {
                delay(100)
                Result.Success(mockNotifications.first())
            }

            viewModel.onPageView()

            advanceUntilIdle()

            viewModel.onCancelDelete()
            verify {
                analyticsClient.notificationCentreCancelDelete()
            }
        }
    }
}