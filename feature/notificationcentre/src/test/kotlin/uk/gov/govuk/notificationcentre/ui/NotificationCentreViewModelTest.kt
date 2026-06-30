package uk.gov.govuk.notificationcentre.ui

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
import uk.gov.govuk.notificationcentre.NotificationCentreUiState
import uk.gov.govuk.notificationcentre.NotificationCentreViewModel
import uk.gov.govuk.notificationcentre.data.NotificationCentreRepo
import uk.gov.govuk.notificationcentre.fixtures.NotificationCentreFixtures.Companion.mockNotifications

@OptIn(ExperimentalCoroutinesApi::class)
class NotificationCentreViewModelTest {
    private val dispatcher = UnconfinedTestDispatcher()

    private val notificationCentreRepo = mockk<NotificationCentreRepo>(relaxed = true)
    private val analyticsClient = mockk<AnalyticsClient>(relaxed = true)

    private lateinit var viewModel: NotificationCentreViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)

        viewModel = NotificationCentreViewModel(notificationCentreRepo, analyticsClient)
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
                    screenClass = "NotificationCentreScreen",
                    screenName = "NotificationCentre",
                    title = "NotificationCentreScreen"
                )
            }
        }
    }

    @Test
    fun `Given view created, then state is Default`() {
        runTest {
            val states = mutableListOf<NotificationCentreUiState>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.uiState.toList(states)
            }

            assertTrue(states[0] is NotificationCentreUiState.Default)
        }
    }

    @Test
    fun `Given view appears, then state transitions to Loading`() {
        runTest {
            coEvery { notificationCentreRepo.getNotifications() } coAnswers {
                delay(100)
                Result.Success(listOf())
            }

            val states = mutableListOf<NotificationCentreUiState>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.uiState.toList(states)
            }
            viewModel.onPageView()

            assertTrue(states.last() is NotificationCentreUiState.Loading)
        }
    }

    @Test
    fun `Given view appears, when offline, transitions to No Internet`() {
        runTest {
            coEvery { notificationCentreRepo.getNotifications() } coAnswers {
                delay(100)
                Result.DeviceOffline()
            }

            val states = mutableListOf<NotificationCentreUiState>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.uiState.toList(states)
            }
            viewModel.onPageView()

            advanceUntilIdle()

            assertTrue(states.last() is NotificationCentreUiState.NoInternet)
        }
    }
    @Test
    fun `Given view appears, when request fails, transitions to Error`() {
        runTest {
            coEvery { notificationCentreRepo.getNotifications() } coAnswers {
                delay(100)
                Result.Error()
            }

            val states = mutableListOf<NotificationCentreUiState>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.uiState.toList(states)
            }
            viewModel.onPageView()

            advanceUntilIdle()

            assertTrue(states.last() is NotificationCentreUiState.Error)
        }
    }

    @Test
    fun `Given view appears, when request returns empty, transitions to Empty`() {
        runTest {
            coEvery { notificationCentreRepo.getNotifications() } coAnswers {
                delay(100)
                Result.Success(listOf())
            }

            val states = mutableListOf<NotificationCentreUiState>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.uiState.toList(states)
            }
            viewModel.onPageView()

            advanceUntilIdle()

            assertTrue(states.last() is NotificationCentreUiState.Empty)
        }
    }

    @Test
    fun `Given view appears, when request returns results, transitions to Loaded and filters into buckets`() {
        runTest {
            coEvery { notificationCentreRepo.getNotifications() } coAnswers {
                delay(100)
                Result.Success(mockNotifications)
            }

            val states = mutableListOf<NotificationCentreUiState>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.uiState.toList(states)
            }
            viewModel.onPageView()

            advanceUntilIdle()

            val lastState = states.last()

            assertTrue(lastState is NotificationCentreUiState.Loaded)

            val notifications = (lastState as NotificationCentreUiState.Loaded).notifications

            assertTrue(notifications.recent.size == 1)
            assertTrue(notifications.recent.contains(mockNotifications[0]))
            assertTrue(notifications.older.size == 1)
            assertTrue(notifications.older.contains(mockNotifications[1]))

        }
    }
}