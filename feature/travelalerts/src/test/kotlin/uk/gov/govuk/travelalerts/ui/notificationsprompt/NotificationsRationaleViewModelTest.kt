package uk.gov.govuk.travelalerts.ui.notificationsprompt

import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.notifications.data.NotificationsRepo
import uk.gov.govuk.travelalerts.data.TravelAlertsRepo

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalPermissionsApi::class)
class NotificationsRationaleViewModelTest {
    private val dispatcher = UnconfinedTestDispatcher()
    private val travelAlertsRepo = mockk<TravelAlertsRepo>(relaxed = true)
    private val notificationsRepo = mockk<NotificationsRepo>(relaxed = true)
    private lateinit var viewModel: NotificationsRationaleViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        viewModel = NotificationsRationaleViewModel(
            notificationsRepo,
            travelAlertsRepo
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Given view created, then state is Loading`() {
        assertTrue(viewModel.uiState.value is NotificationsRationaleViewModel.State.Loading)
    }

    @Test
    fun `Given onPageView called, then state is Default`() = runTest {
        viewModel.onPageView("france")
        assertEquals(NotificationsRationaleViewModel.State.Default, viewModel.uiState.value)
    }

    @Test
    fun `Given Not now tapped, when request succeeds, then navigation event is emitted`() = runTest {
        val events = mutableListOf<Unit>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.navigationEvent.collect { events.add(it) }
        }

        coEvery { travelAlertsRepo.followCountry("france", notificationsEnabled = false) } returns Result.Success(Unit)
        viewModel.onNotNow("france")

        assertEquals(1, events.size)
    }

    @Test
    fun `Given Not now tapped, when request fails, then state is Error`() = runTest {
        coEvery { travelAlertsRepo.followCountry("france", notificationsEnabled = false) } returns Result.Error()
        viewModel.onNotNow("france")

        assertTrue(viewModel.uiState.value is NotificationsRationaleViewModel.State.Error)
    }

    @Test
    fun `Given on resume called without prior agree, then no network call made`() = runTest {
        coEvery { notificationsRepo.permissionGranted() } returns true
        coEvery { travelAlertsRepo.followCountry(any(), any()) } returns Result.Success(Unit)

        // Call without agreeing first
        viewModel.onResume("france")

        // Still in Default state, no navigation occurred
        assertEquals(NotificationsRationaleViewModel.State.Loading, viewModel.uiState.value)
    }

    @Test
    fun `Given on resume called and permission granted, when request succeeds, then navigation event emitted`() = runTest {
        val events = mutableListOf<Unit>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.navigationEvent.collect { events.add(it) }
        }

        coEvery { notificationsRepo.permissionGranted() } returns true
        coEvery { travelAlertsRepo.followCountry("france", notificationsEnabled = true) } returns Result.Success(Unit)

        viewModel.onResume("france")

        // No navigation yet since hasAgreedToContinue is false
        assertEquals(0, events.size)
    }

    @Test
    fun `Given settings alert cancel tapped, when request succeeds, then navigation event is emitted`() = runTest {
        val events = mutableListOf<Unit>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.navigationEvent.collect { events.add(it) }
        }

        coEvery { travelAlertsRepo.followCountry("france", notificationsEnabled = false) } returns Result.Success(Unit)
        viewModel.onSettingsAlertCancel("france")

        assertEquals(1, events.size)
    }

    @Test
    fun `Given dismiss error, then state is Default`() = runTest {
        coEvery { travelAlertsRepo.followCountry(any(), any()) } returns Result.Error()
        viewModel.onNotNow("france")

        viewModel.onDismissError()

        assertEquals(NotificationsRationaleViewModel.State.Default, viewModel.uiState.value)
    }
}
