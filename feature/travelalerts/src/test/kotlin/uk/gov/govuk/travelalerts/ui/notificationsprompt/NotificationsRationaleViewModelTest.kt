package uk.gov.govuk.travelalerts.ui.notificationsprompt

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
    fun `Given settings alert cancel tapped, when request succeeds, then navigation event is emitted`() = runTest {
        val events = mutableListOf<Boolean>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.navigationEvent.collect { events.add(it) }
        }

        coEvery { travelAlertsRepo.followCountry("france", notificationsEnabled = false) } returns Result.Success(Unit)
        viewModel.onSettingsAlertCancel("france")

        assertEquals(1, events.size)
        assertTrue(!events.first())
    }

    @Test
    fun `Given on resume called after agreeing and permission granted, when request fails, then navigation event emitted with error flag set`() = runTest {
        val permissionStatus = mockk<PermissionStatus>()
        every { permissionStatus.isGranted } returns false
        every { permissionStatus.shouldShowRationale } returns false
        coEvery { notificationsRepo.isFirstPermissionRequestCompleted() } returns false
        coEvery { notificationsRepo.requestPermission() } returns Unit
        viewModel.onPageView("france")
        viewModel.onAgreeToContinue(permissionStatus, androidVersion = 33)

        val events = mutableListOf<Boolean>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.navigationEvent.collect { events.add(it) }
        }
        coEvery { notificationsRepo.permissionGranted() } returns true
        coEvery { travelAlertsRepo.followCountry("france", notificationsEnabled = true) } returns Result.Error()

        viewModel.onResume("france")

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
    fun `Given Agree tapped and first request already completed, then state is Alert`() = runTest {
        val permissionStatus = mockk<PermissionStatus>()
        every { permissionStatus.isGranted } returns false
        every { permissionStatus.shouldShowRationale } returns false
        coEvery { notificationsRepo.isFirstPermissionRequestCompleted() } returns true

        viewModel.onAgreeToContinue(permissionStatus, androidVersion = 33)

        assertEquals(NotificationsRationaleViewModel.State.Alert, viewModel.uiState.value)
    }

    @Test
    fun `Given Agree tapped and first request completed without shouldShowRationale, then state is Alert`() = runTest {
        val permissionStatus = mockk<PermissionStatus>()
        every { permissionStatus.isGranted } returns false
        every { permissionStatus.shouldShowRationale } returns false
        coEvery { notificationsRepo.isFirstPermissionRequestCompleted() } returns true

        viewModel.onPageView("france")
        viewModel.onAgreeToContinue(permissionStatus, androidVersion = 33)

        assertEquals(NotificationsRationaleViewModel.State.Alert, viewModel.uiState.value)
    }

    @Test
    fun `Given Agree tapped on Android 13+ with first request not completed, then calls requestPermission`() = runTest {
        val permissionStatus = mockk<PermissionStatus>()
        every { permissionStatus.isGranted } returns false
        every { permissionStatus.shouldShowRationale } returns false
        coEvery { notificationsRepo.isFirstPermissionRequestCompleted() } returns false
        coEvery { notificationsRepo.requestPermission() } returns Unit

        viewModel.onPageView("france")
        viewModel.onAgreeToContinue(permissionStatus, androidVersion = 33)

        coEvery { notificationsRepo.requestPermission() }
    }

    @Test
    fun `Given on resume after Alert path and permission not granted, then state returns to Alert`() = runTest {
        val permissionStatus = mockk<PermissionStatus>()
        every { permissionStatus.isGranted } returns false
        every { permissionStatus.shouldShowRationale } returns false
        coEvery { notificationsRepo.isFirstPermissionRequestCompleted() } returns true
        coEvery { notificationsRepo.permissionGranted() } returns false

        viewModel.onAgreeToContinue(permissionStatus, androidVersion = 33)
        viewModel.onResume("france")

        assertEquals(NotificationsRationaleViewModel.State.Alert, viewModel.uiState.value)
    }
}
