package uk.gov.govuk.notifications.data

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.config.data.flags.FlagRepo
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.data.user.UserRepo
import uk.gov.govuk.data.user.model.ConsentStatus
import uk.gov.govuk.data.user.model.Notifications
import uk.gov.govuk.data.user.model.User
import uk.gov.govuk.notifications.NotificationsProvider
import uk.gov.govuk.notifications.NotificationsSdkState
import uk.gov.govuk.notifications.data.local.NotificationsDataStore

class NotificationsRepoTest {
    private val notificationsDataStore = mockk<NotificationsDataStore>(relaxed = true)
    private val notificationsProvider = mockk<NotificationsProvider>(relaxed = true)
    private val userRepo = mockk<UserRepo>(relaxed = true)
    private val analyticsClient = mockk<AnalyticsClient>(relaxed = true)
    private val flagRepo = mockk<FlagRepo>(relaxed = true)

    // Runs launched work straight away, so background work has finished when a test checks it.
    private val scope = CoroutineScope(UnconfinedTestDispatcher())

    private lateinit var notificationsRepo: NotificationsRepo

    @Before
    fun setup() {
        notificationsRepo = NotificationsRepo(
            notificationsDataStore,
            notificationsProvider,
            userRepo,
            analyticsClient,
            flagRepo,
            scope
        )
    }

    @Test
    fun `Given report inconsistent state, when the SDK state matches the user, then nothing is logged`() {
        every { userRepo.notifications?.pushId } returns "12345"
        every { notificationsProvider.consentGiven() } returns true
        every { notificationsProvider.sdkState() } returns NotificationsSdkState(
            externalId = "12345",
            onesignalId = "onesignal-id",
            pushSubscriptionId = "subscription-id",
            pushOptedIn = false
        )

        notificationsRepo.reportInconsistentState()

        verify(exactly = 0) { analyticsClient.logException(any()) }
    }

    @Test
    fun `Given report inconsistent state, when the external ID is missing, then the flags are logged`() {
        every { userRepo.notifications?.pushId } returns "12345"
        every { notificationsProvider.consentGiven() } returns true
        every { notificationsProvider.sdkState() } returns NotificationsSdkState(
            externalId = "",
            onesignalId = "onesignal-id",
            pushSubscriptionId = "subscription-id",
            pushOptedIn = true
        )

        notificationsRepo.reportInconsistentState()

        verify(exactly = 1) {
            analyticsClient.logException(
                match {
                    it is InconsistentOneSignalStateException &&
                        it.message == "OneSignal state inconsistent on app start: " +
                        "externalIdSet=false, externalIdMatches=false, onesignalIdSet=true, " +
                        "pushSubscriptionIdSet=true, pushOptedIn=true"
                }
            )
        }
    }

    @Test
    fun `Given report inconsistent state, when the user or subscription was never created on the server, then the flags are logged`() {
        every { userRepo.notifications?.pushId } returns "12345"
        every { notificationsProvider.consentGiven() } returns true
        every { notificationsProvider.sdkState() } returns NotificationsSdkState(
            externalId = "12345",
            onesignalId = "",
            pushSubscriptionId = "",
            pushOptedIn = true
        )

        notificationsRepo.reportInconsistentState()

        verify(exactly = 1) {
            analyticsClient.logException(
                match {
                    it is InconsistentOneSignalStateException &&
                        it.message!!.contains("onesignalIdSet=false, pushSubscriptionIdSet=false")
                }
            )
        }
    }

    @Test
    fun `Given report inconsistent state, when consent has not been given, then the SDK is not read`() {
        every { userRepo.notifications?.pushId } returns "12345"
        every { notificationsProvider.consentGiven() } returns false

        notificationsRepo.reportInconsistentState()

        verify(exactly = 0) { notificationsProvider.sdkState() }
        verify(exactly = 0) { analyticsClient.logException(any()) }
    }

    @Test
    fun `Given report inconsistent state, when there is no user state, then the SDK is not read`() {
        every { userRepo.notifications } returns null
        every { notificationsProvider.consentGiven() } returns true

        notificationsRepo.reportInconsistentState()

        verify(exactly = 0) { notificationsProvider.sdkState() }
        verify(exactly = 0) { analyticsClient.logException(any()) }
    }

    @Test
    fun `Given report inconsistent state, when the SDK throws, then the exception is logged`() {
        val exception = IllegalStateException("Must call 'initWithContext' before use")
        every { userRepo.notifications?.pushId } returns "12345"
        every { notificationsProvider.consentGiven() } returns true
        every { notificationsProvider.sdkState() } throws exception

        notificationsRepo.reportInconsistentState()

        verify(exactly = 1) { analyticsClient.logException(exception) }
    }

    @Test
    fun `Given login, when notifications onboarding not completed, then verify correct functions called`() {
        coEvery { notificationsDataStore.isNotificationsOnboardingCompleted() } returns false
        coEvery { userRepo.notifications?.pushId } returns "12345"

        runTest {
            notificationsRepo.login()

            verify(exactly = 1) { notificationsProvider.login("12345") }
            coVerify(exactly = 0) { userRepo.updateNotifications(ConsentStatus.ACCEPTED) }
        }
    }

    @Test
    fun `Given login, when notifications onboarding completed, consent unknown and consent status accepted, then verify correct functions called`() {
        coEvery { notificationsDataStore.isNotificationsOnboardingCompleted() } returns true
        every { notificationsProvider.consentGiven() } returns true
        coEvery { userRepo.notifications?.consentStatus } returns ConsentStatus.UNKNOWN
        coEvery { userRepo.notifications?.pushId } returns "12345"

        runTest {
            notificationsRepo.login()

            verify(exactly = 1) { notificationsProvider.login("12345") }
            // Todo - will be re-added for phase 2 or 3 of Hello UDP
//            coVerify(exactly = 1) { userRepo.updateNotifications(ConsentStatus.ACCEPTED) }
        }
    }

    @Test
    fun `Given login, when notifications onboarding completed, consent unknown and consent status denied, then verify correct functions called`() {
        coEvery { notificationsDataStore.isNotificationsOnboardingCompleted() } returns true
        every { notificationsProvider.consentGiven() } returns false
        coEvery { userRepo.notifications?.consentStatus } returns ConsentStatus.UNKNOWN
        coEvery { userRepo.notifications?.pushId } returns "12345"

        runTest {
            notificationsRepo.login()

            verify(exactly = 1) { notificationsProvider.login("12345") }
            // Todo - will be re-added for phase 2 or 3 of Hello UDP
//            coVerify(exactly = 1) { userRepo.updateNotifications(ConsentStatus.DENIED) }
        }
    }

    @Test
    fun `Given login, when notifications onboarding completed, consent accepted and consent status any, then verify correct functions called`() {
        coEvery { notificationsDataStore.isNotificationsOnboardingCompleted() } returns true
        every { notificationsProvider.consentGiven() } returns false
        coEvery { userRepo.notifications?.consentStatus } returns ConsentStatus.ACCEPTED
        coEvery { userRepo.notifications?.pushId } returns "12345"

        runTest {
            notificationsRepo.login()

            verify(exactly = 1) { notificationsProvider.login("12345") }
            coVerify(exactly = 0) { userRepo.updateNotifications(any()) }
        }
    }

    @Test
    fun `Given login, when notifications is null, then verify correct functions are not called`() {
        coEvery { userRepo.notifications } returns null

        runTest {
            notificationsRepo.login()

            verify(exactly = 0) { notificationsProvider.login(any()) }
            coVerify(exactly = 0) { userRepo.updateNotifications(any()) }
        }
    }

    @Test
    fun `Given logout, then call logout and clear the user`() {
        runTest {
            notificationsRepo.logout()
            verify(exactly = 1) {
                notificationsProvider.logout()
                userRepo.clear()
            }
        }
    }

    @Test
    fun `Given request permission, then call request permission and return result`() {
        coEvery { notificationsProvider.requestPermission() } returns true
        runTest {
            val result = notificationsRepo.requestPermission()
            assertTrue(result)
            coVerify(exactly = 1) {
                notificationsProvider.requestPermission()
            }
        }
    }

    @Test
    fun `Given permission granted, then call request permission`() {
        runTest {
            notificationsRepo.permissionGranted()
            verify(exactly = 1) {
                notificationsProvider.permissionGranted()
            }
        }
    }

    @Test
    fun `Given consent given, then call consent given`() {
        runTest {
            notificationsRepo.consentGiven()
            verify(exactly = 1) {
                notificationsProvider.consentGiven()
            }
        }
    }

    @Test
    fun `Given give consent, then call give consent`() {
        runTest {
            notificationsRepo.giveConsent()
            verify(exactly = 1) {
                notificationsProvider.giveConsent()
            }
        }
    }

    @Test
    fun `Given give consent, when the user state is already loaded, then it is not loaded again`() {
        every { userRepo.notifications } returns Notifications(ConsentStatus.ACCEPTED, "12345")
        every { flagRepo.isFlexEnabled() } returns true

        notificationsRepo.giveConsent()

        coVerify(exactly = 0) { userRepo.initUser() }
    }

    @Test
    fun `Given give consent, when the user state failed to load, then it is loaded and the user is logged in`() {
        every { userRepo.notifications } returnsMany listOf(
            null,
            Notifications(ConsentStatus.ACCEPTED, "12345")
        )
        every { flagRepo.isFlexEnabled() } returns true
        coEvery { userRepo.initUser() } returns Result.Success(mockk<User>(relaxed = true))

        notificationsRepo.giveConsent()

        verify(exactly = 1) { notificationsProvider.giveConsent() }
        coVerify(exactly = 1) { userRepo.initUser() }
        verify(exactly = 1) { notificationsProvider.login("12345") }
    }

    @Test
    fun `Given give consent, when the user state failed to load and loading it again fails, then the user is not logged in`() {
        every { userRepo.notifications } returns null
        every { flagRepo.isFlexEnabled() } returns true
        coEvery { userRepo.initUser() } returns Result.DeviceOffline()

        notificationsRepo.giveConsent()

        coVerify(exactly = 1) { userRepo.initUser() }
        verify(exactly = 0) { notificationsProvider.login(any()) }
    }

    @Test
    fun `Given give consent, when flex is not enabled, then the user state is not loaded`() {
        every { userRepo.notifications } returns null
        every { flagRepo.isFlexEnabled() } returns false

        notificationsRepo.giveConsent()

        coVerify(exactly = 0) { userRepo.initUser() }
    }

    @Test
    fun `Given remove consent, then call remove consent`() {
        runTest {
            notificationsRepo.removeConsent()
            verify(exactly = 1) {
                notificationsProvider.removeConsent()
            }
        }
    }

    @Test
    fun `Given give consent is called, then notifications consent is updated`() {
        runTest {
            notificationsRepo.sendConsent()

            coVerify(exactly = 1) {
                userRepo.updateNotifications(ConsentStatus.ACCEPTED)
            }
        }
    }

    @Test
    fun `Given remove consent is called, then notifications consent is updated`() {
        runTest {
            notificationsRepo.sendRemoveConsent()

            coVerify(exactly = 1) {
                userRepo.updateNotifications(ConsentStatus.DENIED)
            }
        }
    }

    @Test
    fun `Given the user has not previously completed notifications onboarding, When is notifications onboarding completed, then return false`() {
        coEvery { notificationsDataStore.isNotificationsOnboardingCompleted() } returns false

        runTest {
            assertFalse(notificationsRepo.isNotificationsOnboardingCompleted())
        }
    }

    @Test
    fun `Given the user has previously completed notifications onboarding, When is notifications onboarding completed, then return true`() {
        coEvery { notificationsDataStore.isNotificationsOnboardingCompleted() } returns true

        runTest {
            assertTrue(notificationsRepo.isNotificationsOnboardingCompleted())
        }
    }

    @Test
    fun `Given the user has completed notifications onboarding, When notifications onboarding completed, then update data store`() {
        runTest {
            notificationsDataStore.notificationsOnboardingCompleted()

            coVerify(exactly = 1) { notificationsRepo.notificationsOnboardingCompleted() }
            notificationsRepo.firstPermissionRequestCompleted()
            coVerify { notificationsDataStore.firstPermissionRequestCompleted() }
        }
    }

    @Test
    fun `Given the user has not previously requested permission, When is first permission request completed, then return false`() {
        coEvery { notificationsDataStore.isFirstPermissionRequestCompleted() } returns false

        runTest {
            assertFalse(notificationsRepo.isFirstPermissionRequestCompleted())
        }
    }

    @Test
    fun `Given the user has previously requested permission, When is first permission request completed, then return true`() {
        coEvery { notificationsDataStore.isFirstPermissionRequestCompleted() } returns true

        runTest {
            assertTrue(notificationsRepo.isFirstPermissionRequestCompleted())
        }
    }

    @Test
    fun `Given the user has completed first permission request, When first permission request completed, then update data store`() {
        runTest {
            notificationsRepo.firstPermissionRequestCompleted()

            coVerify(exactly = 1) { notificationsDataStore.firstPermissionRequestCompleted() }
        }
    }

    @Test
    fun `Given permission is granted, When checking permission granted, then return true`() {
        every { notificationsProvider.permissionGranted() } returns true

        assertTrue(notificationsRepo.permissionGranted())
    }

    @Test
    fun `Given permission is not granted, When checking permission granted, then return false`() {
        every { notificationsProvider.permissionGranted() } returns false

        assertFalse(notificationsRepo.permissionGranted())
    }

    @Test
    fun `Given consent is given, When checking consent given, then return true`() {
        every { notificationsProvider.consentGiven() } returns true

        assertTrue(notificationsRepo.consentGiven())
    }

    @Test
    fun `Given consent is not given, When checking consent given, then return false`() {
        every { notificationsProvider.consentGiven() } returns false

        assertFalse(notificationsRepo.consentGiven())
    }

    @Test
    fun `When removing consent, then call provider to remove consent`() {
        notificationsRepo.removeConsent()

        verify(exactly = 1) { notificationsProvider.removeConsent() }
    }
}
