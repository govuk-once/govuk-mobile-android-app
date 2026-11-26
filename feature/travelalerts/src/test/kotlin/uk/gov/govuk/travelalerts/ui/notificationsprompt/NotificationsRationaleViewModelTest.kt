package uk.gov.govuk.travelalerts.ui.notificationsprompt

import androidx.lifecycle.SavedStateHandle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.shouldShowRationale
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
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
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.notifications.NotificationsPermissionResolver
import uk.gov.govuk.notifications.data.NotificationsRepo
import uk.gov.govuk.travelalerts.data.TravelAlertsRepo

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalPermissionsApi::class)
class NotificationsRationaleViewModelTest {
    companion object {
        private const val PRIVACY_URL =
            "https://www.gov.uk/government/publications/govuk-app-privacy-notice-how-we-use-your-data"
    }

    private val dispatcher = UnconfinedTestDispatcher()
    private val travelAlertsRepo = mockk<TravelAlertsRepo>(relaxed = true)
    private val notificationsRepo = mockk<NotificationsRepo>(relaxed = true)
    private val permissionResolver = mockk<NotificationsPermissionResolver>()
    private val analyticsClient = mockk<AnalyticsClient>(relaxed = true)
    private val savedStateHandle = SavedStateHandle()
    private lateinit var viewModel: NotificationsRationaleViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        viewModel = NotificationsRationaleViewModel(
            notificationsRepo,
            travelAlertsRepo,
            permissionResolver,
            analyticsClient,
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
        viewModel.onNotNow("france", "Not now")

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
        viewModel.onNotNow("france", "Not now")

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
        viewModel.onNotNow("france", "Not now")

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
        viewModel.onAgreeToContinue(permissionStatus, "Agree and continue", androidVersion = 33)

        assertEquals(1, events.size)
        val event = events.first() as NotificationsRationaleViewModel.NavigationEvent.ExitToTopic
        assertEquals(true, event.error)
    }

    @Test
    fun `Given Agree tapped and resolver returns SETTINGS, then state is Alert`() = runTest {
        val permissionStatus = mockk<PermissionStatus>()
        coEvery { permissionResolver.resolve(permissionStatus, 33) } returns NotificationsPermissionResolver.PermissionPath.SETTINGS

        viewModel.onPageView("france")
        viewModel.onAgreeToContinue(permissionStatus, "Agree and continue", androidVersion = 33)

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
        viewModel.onAgreeToContinue(permissionStatus, "Agree and continue", androidVersion = 33)

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
        viewModel.onAgreeToContinue(permissionStatus, "Agree and continue", androidVersion = 33)
        viewModel.onSettingsAlertContinue()
        viewModel.onResume("france")

        assertEquals(NotificationsRationaleViewModel.State.Default, viewModel.uiState.value)
    }

    @Test
    fun `Given on resume after Settings Continue and permission granted with FOLLOW origin, then followCountry called with true`() =
        runTest {
            val events = mutableListOf<NotificationsRationaleViewModel.NavigationEvent>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.navigationEvent.collect { events.add(it) }
            }

            val permissionStatus = mockk<PermissionStatus>()
            coEvery { permissionResolver.resolve(permissionStatus, 33) } returns NotificationsPermissionResolver.PermissionPath.SETTINGS
            coEvery { notificationsRepo.permissionGranted() } returns true
            coEvery { travelAlertsRepo.followCountry("france", notificationsEnabled = true) } returns Result.Success(Unit)

            viewModel.onPageView("france", NotificationsRationaleViewModel.Origin.FOLLOW)
            viewModel.onAgreeToContinue(permissionStatus, "Agree and continue", androidVersion = 33)
            viewModel.onSettingsAlertContinue()
            viewModel.onResume("france")

            assertEquals(1, events.size)
            val event = events.first() as NotificationsRationaleViewModel.NavigationEvent.ExitToTopic
            assertEquals(false, event.error)
        }

    @Test
    fun `Given on resume after Settings Continue and permission granted with EDIT origin, then toggleNotifications called with true`() =
        runTest {
            val events = mutableListOf<NotificationsRationaleViewModel.NavigationEvent>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.navigationEvent.collect { events.add(it) }
            }

            val permissionStatus = mockk<PermissionStatus>()
            coEvery { permissionResolver.resolve(permissionStatus, 33) } returns NotificationsPermissionResolver.PermissionPath.SETTINGS
            coEvery { notificationsRepo.permissionGranted() } returns true
            coEvery { travelAlertsRepo.toggleNotifications("france", enabled = true) } returns Result.Success(Unit)

            viewModel.onPageView("france", NotificationsRationaleViewModel.Origin.EDIT)
            viewModel.onAgreeToContinue(permissionStatus, "Agree and continue", androidVersion = 33)
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
        viewModel.onAgreeToContinue(permissionStatus, "Agree and continue", androidVersion = 33)
        viewModel.onSettingsAlertCancelClicked()

        assertEquals(NotificationsRationaleViewModel.State.Alert, viewModel.uiState.value)
        verify { analyticsClient.notificationsRationaleSettingsCancel("FOLLOW") }
    }

    @Test
    fun `Given settings alert dismissed after Continue, then no action taken`() = runTest {
        val permissionStatus = mockk<PermissionStatus>()
        coEvery { permissionResolver.resolve(permissionStatus, 33) } returns NotificationsPermissionResolver.PermissionPath.SETTINGS

        viewModel.onPageView("france")
        viewModel.onAgreeToContinue(permissionStatus, "Agree and continue", androidVersion = 33)
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
        viewModel.onAgreeToContinue(permissionStatus, "Agree and continue", androidVersion = 33)
        viewModel.onSettingsAlertDismissed()

        assertEquals(1, events.size)
        val event = events.first() as NotificationsRationaleViewModel.NavigationEvent.ExitToTopic
        assertEquals(false, event.error)
    }

    @Test
    fun `Given onPageView called, then rationale view analytics event is fired with origin`() = runTest {
        viewModel.onPageView("france", NotificationsRationaleViewModel.Origin.EDIT)

        verify { analyticsClient.notificationsRationaleView("EDIT") }
    }

    @Test
    fun `Given settings alert continue tapped, then settings continue analytics event is fired with origin`() = runTest {
        val permissionStatus = mockk<PermissionStatus>()
        coEvery { permissionResolver.resolve(permissionStatus, 33) } returns NotificationsPermissionResolver.PermissionPath.SETTINGS

        viewModel.onPageView("france", NotificationsRationaleViewModel.Origin.FOLLOW)
        viewModel.onAgreeToContinue(permissionStatus, "Agree and continue", androidVersion = 33)
        viewModel.onSettingsAlertContinue()

        verify { analyticsClient.notificationsRationaleSettingsContinue("FOLLOW") }
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
        viewModel.onAgreeToContinue(permissionStatus, "Agree and continue", androidVersion = 33)

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
        viewModel.onAgreeToContinue(permissionStatus, "Agree and continue", androidVersion = 33)

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
        viewModel.onAgreeToContinue(permissionStatus, "Agree and continue", androidVersion = 33)
        viewModel.onSettingsAlertDismissed()

        assertEquals(1, events.size)
        val event = events.first() as NotificationsRationaleViewModel.NavigationEvent.ReturnToEdit
        assertEquals("france", event.slug)
        assertEquals(false, event.error)
        assertEquals(false, event.notificationsEnabled)
    }

    // Analytics — TA-07, TA-08, TA-09

    @Test
    fun `Given Agree tapped, then allow notifications analytics event is fired`() = runTest {
        val permissionStatus = mockk<PermissionStatus>()
        coEvery { permissionResolver.resolve(permissionStatus, 33) } returns NotificationsPermissionResolver.PermissionPath.GRANTED
        coEvery { travelAlertsRepo.followCountry("france", notificationsEnabled = true) } returns Result.Success(Unit)
        viewModel.onPageView("france")

        viewModel.onAgreeToContinue(permissionStatus, "Agree and continue", androidVersion = 33)

        verify {
            analyticsClient.buttonClick(
                text = "Agree and continue",
                external = false,
                section = "Travel Abroad Notifications"
            )
        }
    }

    @Test
    fun `Given Not now tapped on permission screen, then not now analytics event is fired`() = runTest {
        coEvery { travelAlertsRepo.followCountry("france", notificationsEnabled = false) } returns Result.Success(Unit)
        viewModel.onPageView("france")

        viewModel.onNotNow("france", "Not now")

        verify {
            analyticsClient.buttonClick(
                text = "Not now",
                external = false,
                section = "Travel Abroad Notifications"
            )
        }
    }

    @Test
    fun `Given privacy link tapped, then read privacy notice analytics event is fired as external with url`() {
        viewModel.onPrivacyLinkClick("Read the privacy notice", PRIVACY_URL)

        verify {
            analyticsClient.buttonClick(
                text = "Read the privacy notice",
                url = PRIVACY_URL,
                external = true,
                section = "Travel Abroad Notifications"
            )
        }
    }

    // Notification permission result

    @Test
    fun `Given OS prompt granted, then System permission result Granted is tracked`() = runTest {
        val permissionStatus = mockk<PermissionStatus>()
        coEvery { permissionResolver.resolve(permissionStatus, 33) } returns NotificationsPermissionResolver.PermissionPath.OS_PROMPT
        coEvery { permissionResolver.requestOsPermission() } returns true
        coEvery { travelAlertsRepo.followCountry("france", notificationsEnabled = true) } returns Result.Success(Unit)

        viewModel.onPageView("france", NotificationsRationaleViewModel.Origin.FOLLOW)
        viewModel.onAgreeToContinue(permissionStatus, "Agree and continue", androidVersion = 33)

        verify(exactly = 1) {
            analyticsClient.notificationPermissionResult(
                type = "System",
                granted = true,
                section = "Travel Abroad Notifications"
            )
        }
    }

    @Test
    fun `Given OS prompt denied, then System permission result Denied is tracked`() = runTest {
        val permissionStatus = mockk<PermissionStatus>()
        coEvery { permissionResolver.resolve(permissionStatus, 33) } returns NotificationsPermissionResolver.PermissionPath.OS_PROMPT
        coEvery { permissionResolver.requestOsPermission() } returns false
        coEvery { travelAlertsRepo.toggleNotifications("france", enabled = false) } returns Result.Success(Unit)

        viewModel.onPageView("france", NotificationsRationaleViewModel.Origin.EDIT)
        viewModel.onAgreeToContinue(permissionStatus, "Agree and continue", androidVersion = 33)

        verify(exactly = 1) {
            analyticsClient.notificationPermissionResult(
                type = "System",
                granted = false,
                section = "Travel Abroad Notifications"
            )
        }
    }

    @Test
    fun `Given permission already granted, when Agree tapped, then no permission result is tracked`() = runTest {
        val permissionStatus = mockk<PermissionStatus>()
        coEvery { permissionResolver.resolve(permissionStatus, 33) } returns NotificationsPermissionResolver.PermissionPath.GRANTED
        coEvery { travelAlertsRepo.followCountry("france", notificationsEnabled = true) } returns Result.Success(Unit)

        viewModel.onPageView("france", NotificationsRationaleViewModel.Origin.FOLLOW)
        viewModel.onAgreeToContinue(permissionStatus, "Agree and continue", androidVersion = 33)

        verify(exactly = 0) { analyticsClient.notificationPermissionResult(any(), any(), any()) }
    }

    @Test
    fun `Given Settings alert shown, when not yet continued, then no permission result is tracked`() = runTest {
        val permissionStatus = mockk<PermissionStatus>()
        coEvery { permissionResolver.resolve(permissionStatus, 33) } returns NotificationsPermissionResolver.PermissionPath.SETTINGS
        coEvery { notificationsRepo.permissionGranted() } returns false

        viewModel.onPageView("france")
        viewModel.onAgreeToContinue(permissionStatus, "Agree and continue", androidVersion = 33)
        viewModel.onResume("france")

        verify(exactly = 0) { analyticsClient.notificationPermissionResult(any(), any(), any()) }
    }

    @Test
    fun `Given return from Settings with permission granted, then Settings permission result Granted is tracked`() = runTest {
        val permissionStatus = mockk<PermissionStatus>()
        coEvery { permissionResolver.resolve(permissionStatus, 33) } returns NotificationsPermissionResolver.PermissionPath.SETTINGS
        coEvery { notificationsRepo.permissionGranted() } returns true
        coEvery { travelAlertsRepo.followCountry("france", notificationsEnabled = true) } returns Result.Success(Unit)

        viewModel.onPageView("france", NotificationsRationaleViewModel.Origin.FOLLOW)
        viewModel.onAgreeToContinue(permissionStatus, "Agree and continue", androidVersion = 33)
        viewModel.onSettingsAlertContinue()
        viewModel.onResume("france")

        verify(exactly = 1) {
            analyticsClient.notificationPermissionResult(
                type = "Settings",
                granted = true,
                section = "Travel Abroad Notifications"
            )
        }
    }

    @Test
    fun `Given return from Settings still denied, when resumed again, then Settings permission result Denied is tracked once`() = runTest {
        val permissionStatus = mockk<PermissionStatus>()
        coEvery { permissionResolver.resolve(permissionStatus, 33) } returns NotificationsPermissionResolver.PermissionPath.SETTINGS
        coEvery { notificationsRepo.permissionGranted() } returns false

        viewModel.onPageView("france")
        viewModel.onAgreeToContinue(permissionStatus, "Agree and continue", androidVersion = 33)
        viewModel.onSettingsAlertContinue()
        viewModel.onResume("france")
        viewModel.onResume("france")
        viewModel.onResume("france")

        verify(exactly = 1) {
            analyticsClient.notificationPermissionResult(
                type = "Settings",
                granted = false,
                section = "Travel Abroad Notifications"
            )
        }
    }

    @Test
    fun `Given a second trip to Settings, then Settings permission result is tracked for each trip`() = runTest {
        val permissionStatus = mockk<PermissionStatus>()
        coEvery { permissionResolver.resolve(permissionStatus, 33) } returns NotificationsPermissionResolver.PermissionPath.SETTINGS
        coEvery { notificationsRepo.permissionGranted() } returns false

        viewModel.onPageView("france")
        viewModel.onAgreeToContinue(permissionStatus, "Agree and continue", androidVersion = 33)
        viewModel.onSettingsAlertContinue()
        viewModel.onResume("france")
        viewModel.onAgreeToContinue(permissionStatus, "Agree and continue", androidVersion = 33)
        viewModel.onSettingsAlertContinue()
        viewModel.onResume("france")

        verify(exactly = 2) {
            analyticsClient.notificationPermissionResult(
                type = "Settings",
                granted = false,
                section = "Travel Abroad Notifications"
            )
        }
    }

    @Test
    fun `Given on resume after Settings Continue and permission denied with EDIT origin, then state returns to Default`() = runTest {
        val permissionStatus = mockk<PermissionStatus>()
        coEvery { permissionResolver.resolve(permissionStatus, 33) } returns NotificationsPermissionResolver.PermissionPath.SETTINGS
        coEvery { notificationsRepo.permissionGranted() } returns false

        viewModel.onPageView("france", NotificationsRationaleViewModel.Origin.EDIT)
        viewModel.onAgreeToContinue(permissionStatus, "Agree and continue", androidVersion = 33)
        viewModel.onSettingsAlertContinue()
        viewModel.onResume("france")

        assertEquals(NotificationsRationaleViewModel.State.Default, viewModel.uiState.value)
    }
}
