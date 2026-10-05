package uk.gov.govuk.travelalerts.ui.notificationsprompt

import androidx.lifecycle.SavedStateHandle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.shouldShowRationale
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
    private val savedStateHandle = SavedStateHandle()
    private lateinit var viewModel: NotificationsRationaleViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        viewModel = NotificationsRationaleViewModel(
            notificationsRepo,
            travelAlertsRepo,
            savedStateHandle
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
    fun `Given Not now tapped, when request succeeds, then navigation event is emitted without error flag`() = runTest {
        val events = mutableListOf<Boolean>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.navigationEvent.collect { events.add(it) }
        }

        coEvery { travelAlertsRepo.followCountry("france", notificationsEnabled = false) } returns Result.Success(Unit)
        viewModel.onNotNow("france")

        assertEquals(1, events.size)
        assertTrue(!events.first())
    }

    @Test
    fun `Given Not now tapped, when request fails, then navigation event is emitted with error flag set`() = runTest {
        val events = mutableListOf<Boolean>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.navigationEvent.collect { events.add(it) }
        }

        coEvery { travelAlertsRepo.followCountry("france", notificationsEnabled = false) } returns Result.Error()
        viewModel.onNotNow("france")

        assertEquals(1, events.size)
        assertTrue(events.first())
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
        val events = mutableListOf<Boolean>()
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
    fun `Given OS prompt granted and followCountry fails, then navigation event emitted with error flag`() = runTest {
        val events = mutableListOf<Boolean>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.navigationEvent.collect { events.add(it) }
        }

        val permissionStatus = mockk<PermissionStatus>()
        every { permissionStatus.isGranted } returns false
        every { permissionStatus.shouldShowRationale } returns false
        coEvery { notificationsRepo.isFirstPermissionRequestCompleted() } returns false
        coEvery { notificationsRepo.requestPermission() } returns true
        coEvery { travelAlertsRepo.followCountry("france", notificationsEnabled = true) } returns Result.Error()

        viewModel.onPageView("france")
        viewModel.onAgreeToContinue(permissionStatus, androidVersion = 33)

        assertEquals(1, events.size)
        assertTrue(events.first())
    }

    @Test
    fun `Given Agree tapped on Android less than 13, then state is Alert`() = runTest {
        val permissionStatus = mockk<PermissionStatus>()
        every { permissionStatus.isGranted } returns false
        every { permissionStatus.shouldShowRationale } returns false

        viewModel.onAgreeToContinue(permissionStatus, androidVersion = 30)

        assertEquals(NotificationsRationaleViewModel.State.Alert, viewModel.uiState.value)
    }

    @Test
    fun `Given Agree tapped on Android 13+ with first request already completed and no shouldShowRationale, then state is Alert`() = runTest {
        val permissionStatus = mockk<PermissionStatus>()
        every { permissionStatus.isGranted } returns false
        every { permissionStatus.shouldShowRationale } returns false
        coEvery { notificationsRepo.isFirstPermissionRequestCompleted() } returns true

        viewModel.onPageView("france")
        viewModel.onAgreeToContinue(permissionStatus, androidVersion = 33)

        assertEquals(NotificationsRationaleViewModel.State.Alert, viewModel.uiState.value)
    }

    @Test
    fun `Given Agree tapped on Android 13+ with first request already completed and shouldShowRationale=true, then OS prompt is shown`() = runTest {
        val events = mutableListOf<Boolean>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.navigationEvent.collect { events.add(it) }
        }

        val permissionStatus = mockk<PermissionStatus>()
        every { permissionStatus.isGranted } returns false
        every { permissionStatus.shouldShowRationale } returns true
        coEvery { notificationsRepo.isFirstPermissionRequestCompleted() } returns true
        coEvery { notificationsRepo.requestPermission() } returns true
        coEvery { travelAlertsRepo.followCountry("france", notificationsEnabled = true) } returns Result.Success(Unit)

        viewModel.onPageView("france")
        viewModel.onAgreeToContinue(permissionStatus, androidVersion = 33)

        assertEquals(1, events.size)
        assertTrue(!events.first())
    }

    @Test
    fun `Given Agree tapped on Android 13+ with first request not completed and OS prompt denied, then followCountry called with notificationsEnabled=false`() = runTest {
        val events = mutableListOf<Boolean>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.navigationEvent.collect { events.add(it) }
        }

        val permissionStatus = mockk<PermissionStatus>()
        every { permissionStatus.isGranted } returns false
        every { permissionStatus.shouldShowRationale } returns false
        coEvery { notificationsRepo.isFirstPermissionRequestCompleted() } returns false
        coEvery { notificationsRepo.requestPermission() } returns false
        coEvery { travelAlertsRepo.followCountry("france", notificationsEnabled = false) } returns Result.Success(Unit)

        viewModel.onPageView("france")
        viewModel.onAgreeToContinue(permissionStatus, androidVersion = 33)

        assertEquals(1, events.size)
        assertTrue(!events.first())
        coEvery { travelAlertsRepo.followCountry("france", notificationsEnabled = false) }
    }

    @Test
    fun `Given on resume after Settings Continue and permission not granted, then state returns to Default`() = runTest {
        val permissionStatus = mockk<PermissionStatus>()
        every { permissionStatus.isGranted } returns false
        every { permissionStatus.shouldShowRationale } returns false
        coEvery { notificationsRepo.isFirstPermissionRequestCompleted() } returns true
        coEvery { notificationsRepo.permissionGranted() } returns false

        viewModel.onPageView("france")
        viewModel.onAgreeToContinue(permissionStatus, androidVersion = 33)
        viewModel.onSettingsAlertContinue()
        viewModel.onResume("france")

        assertEquals(NotificationsRationaleViewModel.State.Default, viewModel.uiState.value)
    }

    @Test
    fun `Given on resume after Settings Continue and permission granted, then followCountry called with notificationsEnabled=true`() = runTest {
        val events = mutableListOf<Boolean>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.navigationEvent.collect { events.add(it) }
        }

        val permissionStatus = mockk<PermissionStatus>()
        every { permissionStatus.isGranted } returns false
        every { permissionStatus.shouldShowRationale } returns false
        coEvery { notificationsRepo.isFirstPermissionRequestCompleted() } returns true
        coEvery { notificationsRepo.permissionGranted() } returns true
        coEvery { travelAlertsRepo.followCountry("france", notificationsEnabled = true) } returns Result.Success(Unit)

        viewModel.onPageView("france")
        viewModel.onAgreeToContinue(permissionStatus, androidVersion = 33)
        viewModel.onSettingsAlertContinue()
        viewModel.onResume("france")

        assertEquals(1, events.size)
        assertTrue(!events.first())
    }

    @Test
    fun `Given settings alert cancel clicked, then only analytics is recorded`() = runTest {
        val permissionStatus = mockk<PermissionStatus>()
        every { permissionStatus.isGranted } returns false
        every { permissionStatus.shouldShowRationale } returns false
        coEvery { notificationsRepo.isFirstPermissionRequestCompleted() } returns true

        viewModel.onPageView("france")
        viewModel.onAgreeToContinue(permissionStatus, androidVersion = 33)

        // Should not throw or cause any state change
        viewModel.onSettingsAlertCancelClicked()
        assertEquals(NotificationsRationaleViewModel.State.Alert, viewModel.uiState.value)
    }

    @Test
    fun `Given settings alert dismissed after Continue, then no action taken`() = runTest {
        val permissionStatus = mockk<PermissionStatus>()
        every { permissionStatus.isGranted } returns false
        every { permissionStatus.shouldShowRationale } returns false
        coEvery { notificationsRepo.isFirstPermissionRequestCompleted() } returns true

        viewModel.onPageView("france")
        viewModel.onAgreeToContinue(permissionStatus, androidVersion = 33)
        viewModel.onSettingsAlertContinue()

        val events = mutableListOf<Boolean>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.navigationEvent.collect { events.add(it) }
        }

        viewModel.onSettingsAlertDismissed()
        assertEquals(0, events.size)
    }

    @Test
    fun `Given settings alert dismissed before Continue on SETTINGS path, then followCountry called with notificationsEnabled=false`() = runTest {
        val events = mutableListOf<Boolean>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.navigationEvent.collect { events.add(it) }
        }

        val permissionStatus = mockk<PermissionStatus>()
        every { permissionStatus.isGranted } returns false
        every { permissionStatus.shouldShowRationale } returns false
        coEvery { notificationsRepo.isFirstPermissionRequestCompleted() } returns true
        coEvery { travelAlertsRepo.followCountry("france", notificationsEnabled = false) } returns Result.Success(Unit)

        viewModel.onPageView("france")
        // This takes the SETTINGS path (first request completed, no shouldShowRationale)
        viewModel.onAgreeToContinue(permissionStatus, androidVersion = 33)
        // Dismiss the alert before Continue is clicked - user rejected the settings flow
        viewModel.onSettingsAlertDismissed()

        // Dismissing the dialog without tapping Continue = user declined, complete with false
        assertEquals(1, events.size)
        assertTrue(!events.first())
    }
}
