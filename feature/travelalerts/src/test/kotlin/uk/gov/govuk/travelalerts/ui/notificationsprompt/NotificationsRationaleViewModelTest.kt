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
import uk.gov.govuk.analytics.AnalyticsCoordinatorInterface
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.notifications.NotificationsPermissionResolver
import uk.gov.govuk.notifications.data.NotificationsRepo
import uk.gov.govuk.travelalerts.data.TravelAlertsRepo

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalPermissionsApi::class)
class NotificationsRationaleViewModelTest {
    private val dispatcher = UnconfinedTestDispatcher()
    private val travelAlertsRepo = mockk<TravelAlertsRepo>(relaxed = true)
    private val notificationsRepo = mockk<NotificationsRepo>(relaxed = true)
    private val permissionResolver = mockk<NotificationsPermissionResolver>()
    private val analyticsCoordinator = mockk<AnalyticsCoordinatorInterface>(relaxed = true)
    private val savedStateHandle = SavedStateHandle()
    private lateinit var viewModel: NotificationsRationaleViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        viewModel = NotificationsRationaleViewModel(
            notificationsRepo,
            travelAlertsRepo,
            permissionResolver,
            analyticsCoordinator,
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
    fun `Given onPageView called with FOLLOW origin, then state is Default`() = runTest {
        viewModel.onPageView("france", NotificationsRationaleViewModel.Origin.FOLLOW)
        assertEquals(NotificationsRationaleViewModel.State.Default, viewModel.uiState.value)
    }

    @Test
    fun `Given Not now tapped with FOLLOW origin and request succeeds, then ExitToTopic without error`() = runTest {
        val events = mutableListOf<NotificationsRationaleViewModel.NavigationEvent>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.navigationEvent.collect { events.add(it) }
        }

        coEvery { travelAlertsRepo.followCountry("france", notificationsEnabled = false) } returns Result.Success(Unit)
        viewModel.onPageView("france", NotificationsRationaleViewModel.Origin.FOLLOW)
        viewModel.onNotNow("france")

        assertEquals(1, events.size)
        val event = events.first() as NotificationsRationaleViewModel.NavigationEvent.ExitToTopic
        assertEquals(false, event.error)
    }

    @Test
    fun `Given Not now tapped with FOLLOW origin and request fails, then ExitToTopic with error`() = runTest {
        val events = mutableListOf<NotificationsRationaleViewModel.NavigationEvent>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.navigationEvent.collect { events.add(it) }
        }

        coEvery { travelAlertsRepo.followCountry("france", notificationsEnabled = false) } returns Result.Error()
        viewModel.onPageView("france", NotificationsRationaleViewModel.Origin.FOLLOW)
        viewModel.onNotNow("france")

        assertEquals(1, events.size)
        val event = events.first() as NotificationsRationaleViewModel.NavigationEvent.ExitToTopic
        assertEquals(true, event.error)
    }

    @Test
    fun `Given Not now tapped with EDIT origin, then ReturnToEdit without error`() = runTest {
        val events = mutableListOf<NotificationsRationaleViewModel.NavigationEvent>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.navigationEvent.collect { events.add(it) }
        }

        viewModel.onPageView("france", NotificationsRationaleViewModel.Origin.EDIT)
        viewModel.onNotNow("france")

        assertEquals(1, events.size)
        val event = events.first() as NotificationsRationaleViewModel.NavigationEvent.ReturnToEdit
        assertEquals("france", event.slug)
        assertEquals(false, event.error)
        assertEquals(false, event.notificationsEnabled)
    }

    @Test
    fun `Given on resume called without prior agree, then no network call made`() = runTest {
        coEvery { notificationsRepo.permissionGranted() } returns true

        viewModel.onResume("france")

        assertEquals(NotificationsRationaleViewModel.State.Loading, viewModel.uiState.value)
    }

    @Test
    fun `Given OS prompt granted with FOLLOW origin and followCountry fails, then ExitToTopic with error`() = runTest {
        val events = mutableListOf<NotificationsRationaleViewModel.NavigationEvent>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.navigationEvent.collect { events.add(it) }
        }

        val permissionStatus = mockk<PermissionStatus>()
        every { permissionStatus.isGranted } returns false
        every { permissionStatus.shouldShowRationale } returns false
        coEvery { permissionResolver.resolve(permissionStatus, 33) } returns NotificationsPermissionResolver.PermissionPath.OS_PROMPT
        coEvery { permissionResolver.requestOsPermission() } returns true
        coEvery { travelAlertsRepo.followCountry("france", notificationsEnabled = true) } returns Result.Error()

        viewModel.onPageView("france", NotificationsRationaleViewModel.Origin.FOLLOW)
        viewModel.onAgreeToContinue(permissionStatus, androidVersion = 33)

        assertEquals(1, events.size)
        val event = events.first() as NotificationsRationaleViewModel.NavigationEvent.ExitToTopic
        assertEquals(true, event.error)
    }

    @Test
    fun `Given Agree tapped and resolver returns SETTINGS, then state is Alert`() = runTest {
        val permissionStatus = mockk<PermissionStatus>()
        coEvery { permissionResolver.resolve(permissionStatus, 33) } returns NotificationsPermissionResolver.PermissionPath.SETTINGS

        viewModel.onPageView("france")
        viewModel.onAgreeToContinue(permissionStatus, androidVersion = 33)

        assertEquals(NotificationsRationaleViewModel.State.Alert, viewModel.uiState.value)
    }

    @Test
    fun `Given Agree tapped and resolver returns GRANTED, then followCountry called with true`() = runTest {
        val events = mutableListOf<NotificationsRationaleViewModel.NavigationEvent>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.navigationEvent.collect { events.add(it) }
        }

        val permissionStatus = mockk<PermissionStatus>()
        coEvery { permissionResolver.resolve(permissionStatus, 33) } returns NotificationsPermissionResolver.PermissionPath.GRANTED
        coEvery { travelAlertsRepo.followCountry("france", notificationsEnabled = true) } returns Result.Success(Unit)

        viewModel.onPageView("france", NotificationsRationaleViewModel.Origin.FOLLOW)
        viewModel.onAgreeToContinue(permissionStatus, androidVersion = 33)

        assertEquals(1, events.size)
        val event = events.first() as NotificationsRationaleViewModel.NavigationEvent.ExitToTopic
        assertEquals(false, event.error)
    }

    @Test
    fun `Given on resume after Settings Continue and permission not granted, then state returns to Default`() = runTest {
        val permissionStatus = mockk<PermissionStatus>()
        coEvery { permissionResolver.resolve(permissionStatus, 33) } returns NotificationsPermissionResolver.PermissionPath.SETTINGS
        coEvery { notificationsRepo.permissionGranted() } returns false

        viewModel.onPageView("france")
        viewModel.onAgreeToContinue(permissionStatus, androidVersion = 33)
        viewModel.onSettingsAlertContinue()
        viewModel.onResume("france")

        assertEquals(NotificationsRationaleViewModel.State.Default, viewModel.uiState.value)
    }

    @Test
    fun `Given on resume after Settings Continue and permission granted with FOLLOW origin, then followCountry called with true`() = runTest {
        val events = mutableListOf<NotificationsRationaleViewModel.NavigationEvent>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.navigationEvent.collect { events.add(it) }
        }

        val permissionStatus = mockk<PermissionStatus>()
        coEvery { permissionResolver.resolve(permissionStatus, 33) } returns NotificationsPermissionResolver.PermissionPath.SETTINGS
        coEvery { notificationsRepo.permissionGranted() } returns true
        coEvery { travelAlertsRepo.followCountry("france", notificationsEnabled = true) } returns Result.Success(Unit)

        viewModel.onPageView("france", NotificationsRationaleViewModel.Origin.FOLLOW)
        viewModel.onAgreeToContinue(permissionStatus, androidVersion = 33)
        viewModel.onSettingsAlertContinue()
        viewModel.onResume("france")

        assertEquals(1, events.size)
        val event = events.first() as NotificationsRationaleViewModel.NavigationEvent.ExitToTopic
        assertEquals(false, event.error)
    }

    @Test
    fun `Given on resume after Settings Continue and permission granted with EDIT origin, then toggleNotifications called with true`() = runTest {
        val events = mutableListOf<NotificationsRationaleViewModel.NavigationEvent>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.navigationEvent.collect { events.add(it) }
        }

        val permissionStatus = mockk<PermissionStatus>()
        coEvery { permissionResolver.resolve(permissionStatus, 33) } returns NotificationsPermissionResolver.PermissionPath.SETTINGS
        coEvery { notificationsRepo.permissionGranted() } returns true
        coEvery { travelAlertsRepo.toggleNotifications("france", enabled = true) } returns Result.Success(Unit)

        viewModel.onPageView("france", NotificationsRationaleViewModel.Origin.EDIT)
        viewModel.onAgreeToContinue(permissionStatus, androidVersion = 33)
        viewModel.onSettingsAlertContinue()
        viewModel.onResume("france")

        assertEquals(1, events.size)
        val event = events.first() as NotificationsRationaleViewModel.NavigationEvent.ReturnToEdit
        assertEquals("france", event.slug)
        assertEquals(false, event.error)
        assertEquals(true, event.notificationsEnabled)
    }

    @Test
    fun `Given settings alert cancel clicked, then only analytics is recorded`() = runTest {
        val permissionStatus = mockk<PermissionStatus>()
        coEvery { permissionResolver.resolve(permissionStatus, 33) } returns NotificationsPermissionResolver.PermissionPath.SETTINGS

        viewModel.onPageView("france")
        viewModel.onAgreeToContinue(permissionStatus, androidVersion = 33)
        viewModel.onSettingsAlertCancelClicked()

        assertEquals(NotificationsRationaleViewModel.State.Alert, viewModel.uiState.value)
    }

    @Test
    fun `Given settings alert dismissed after Continue, then no action taken`() = runTest {
        val permissionStatus = mockk<PermissionStatus>()
        coEvery { permissionResolver.resolve(permissionStatus, 33) } returns NotificationsPermissionResolver.PermissionPath.SETTINGS

        viewModel.onPageView("france")
        viewModel.onAgreeToContinue(permissionStatus, androidVersion = 33)
        viewModel.onSettingsAlertContinue()

        val events = mutableListOf<NotificationsRationaleViewModel.NavigationEvent>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.navigationEvent.collect { events.add(it) }
        }

        viewModel.onSettingsAlertDismissed()
        assertEquals(0, events.size)
    }

    @Test
    fun `Given settings alert dismissed before Continue, then followCountry called with false`() = runTest {
        val events = mutableListOf<NotificationsRationaleViewModel.NavigationEvent>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.navigationEvent.collect { events.add(it) }
        }

        val permissionStatus = mockk<PermissionStatus>()
        coEvery { permissionResolver.resolve(permissionStatus, 33) } returns NotificationsPermissionResolver.PermissionPath.SETTINGS
        coEvery { travelAlertsRepo.followCountry("france", notificationsEnabled = false) } returns Result.Success(Unit)

        viewModel.onPageView("france", NotificationsRationaleViewModel.Origin.FOLLOW)
        viewModel.onAgreeToContinue(permissionStatus, androidVersion = 33)
        viewModel.onSettingsAlertDismissed()

        assertEquals(1, events.size)
        val event = events.first() as NotificationsRationaleViewModel.NavigationEvent.ExitToTopic
        assertEquals(false, event.error)
    }

    @Test
    fun `Given onPageView called with EDIT origin, then analytics is recorded with EDIT`() = runTest {
        viewModel.onPageView("france", NotificationsRationaleViewModel.Origin.EDIT)
        assertEquals(NotificationsRationaleViewModel.State.Default, viewModel.uiState.value)
    }

    @Test
    fun `Given Agree tapped with GRANTED path and EDIT origin, then toggleNotifications called with true`() = runTest {
        val events = mutableListOf<NotificationsRationaleViewModel.NavigationEvent>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.navigationEvent.collect { events.add(it) }
        }

        val permissionStatus = mockk<PermissionStatus>()
        coEvery { permissionResolver.resolve(permissionStatus, 33) } returns NotificationsPermissionResolver.PermissionPath.GRANTED
        coEvery { travelAlertsRepo.toggleNotifications("france", enabled = true) } returns Result.Success(Unit)

        viewModel.onPageView("france", NotificationsRationaleViewModel.Origin.EDIT)
        viewModel.onAgreeToContinue(permissionStatus, androidVersion = 33)

        assertEquals(1, events.size)
        val event = events.first() as NotificationsRationaleViewModel.NavigationEvent.ReturnToEdit
        assertEquals("france", event.slug)
        assertEquals(false, event.error)
        assertEquals(true, event.notificationsEnabled)
    }

    @Test
    fun `Given OS prompt denied with EDIT origin, then toggleNotifications called with false`() = runTest {
        val events = mutableListOf<NotificationsRationaleViewModel.NavigationEvent>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.navigationEvent.collect { events.add(it) }
        }

        val permissionStatus = mockk<PermissionStatus>()
        every { permissionStatus.isGranted } returns false
        every { permissionStatus.shouldShowRationale } returns false
        coEvery { permissionResolver.resolve(permissionStatus, 33) } returns NotificationsPermissionResolver.PermissionPath.OS_PROMPT
        coEvery { permissionResolver.requestOsPermission() } returns false
        coEvery { travelAlertsRepo.toggleNotifications("france", enabled = false) } returns Result.Success(Unit)

        viewModel.onPageView("france", NotificationsRationaleViewModel.Origin.EDIT)
        viewModel.onAgreeToContinue(permissionStatus, androidVersion = 33)

        assertEquals(1, events.size)
        val event = events.first() as NotificationsRationaleViewModel.NavigationEvent.ReturnToEdit
        assertEquals("france", event.slug)
        assertEquals(false, event.error)
        assertEquals(false, event.notificationsEnabled)
    }

    @Test
    fun `Given settings alert dismissed before Continue with EDIT origin, then toggleNotifications called with false`() = runTest {
        val events = mutableListOf<NotificationsRationaleViewModel.NavigationEvent>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.navigationEvent.collect { events.add(it) }
        }

        val permissionStatus = mockk<PermissionStatus>()
        coEvery { permissionResolver.resolve(permissionStatus, 33) } returns NotificationsPermissionResolver.PermissionPath.SETTINGS
        coEvery { travelAlertsRepo.toggleNotifications("france", enabled = false) } returns Result.Success(Unit)

        viewModel.onPageView("france", NotificationsRationaleViewModel.Origin.EDIT)
        viewModel.onAgreeToContinue(permissionStatus, androidVersion = 33)
        viewModel.onSettingsAlertDismissed()

        assertEquals(1, events.size)
        val event = events.first() as NotificationsRationaleViewModel.NavigationEvent.ReturnToEdit
        assertEquals("france", event.slug)
        assertEquals(false, event.error)
        assertEquals(false, event.notificationsEnabled)
    }

    @Test
    fun `Given on resume after Settings Continue and permission denied with EDIT origin, then state returns to Default`() = runTest {
        val permissionStatus = mockk<PermissionStatus>()
        coEvery { permissionResolver.resolve(permissionStatus, 33) } returns NotificationsPermissionResolver.PermissionPath.SETTINGS
        coEvery { notificationsRepo.permissionGranted() } returns false

        viewModel.onPageView("france", NotificationsRationaleViewModel.Origin.EDIT)
        viewModel.onAgreeToContinue(permissionStatus, androidVersion = 33)
        viewModel.onSettingsAlertContinue()
        viewModel.onResume("france")

        assertEquals(NotificationsRationaleViewModel.State.Default, viewModel.uiState.value)
    }
}
