package uk.gov.govuk.notifications.data

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.data.user.UserRepo
import uk.gov.govuk.data.user.model.ConsentStatus
import uk.gov.govuk.notifications.NotificationsProvider
import uk.gov.govuk.notifications.data.local.NotificationsDataStore

class NotificationsRepoTest {
    private val notificationsDataStore = mockk<NotificationsDataStore>(relaxed = true)
    private val notificationsProvider = mockk<NotificationsProvider>(relaxed = true)
    private val userRepo = mockk<UserRepo>(relaxed = true)

    private lateinit var notificationsRepo: NotificationsRepo

    @Before
    fun setup() {
        notificationsRepo = NotificationsRepo(
            notificationsDataStore,
            notificationsProvider,
            userRepo
        )
    }

    @Test
    fun `Given login, when notifications onboarding not completed, then verify correct functions called`() {
        coEvery { notificationsDataStore.isNotificationsOnboardingCompleted() } returns false
        coEvery { userRepo.notificationId } returns "12345"

        runTest {
            notificationsRepo.login()

            verify(exactly = 1) { notificationsProvider.login("12345") }
            coVerify(exactly = 0) { userRepo.updateNotifications(ConsentStatus.ACCEPTED) }
        }
    }

    @Test
    fun `Given login, when notifications onboarding completed, consent preference unknown and consent status accepted, then verify correct functions called`() {
        coEvery { notificationsDataStore.isNotificationsOnboardingCompleted() } returns true
        coEvery { userRepo.notificationId } returns "12345"
        every { notificationsProvider.consentGiven() } returns true
        coEvery { userRepo.preferences?.notifications?.consentStatus } returns ConsentStatus.UNKNOWN

        runTest {
            notificationsRepo.login()

            verify(exactly = 1) { notificationsProvider.login("12345") }
            coVerify(exactly = 1) { userRepo.updateNotifications(ConsentStatus.ACCEPTED) }
        }
    }

    @Test
    fun `Given login, when notifications onboarding completed, consent preference unknown and consent status denied, then verify correct functions called`() {
        coEvery { notificationsDataStore.isNotificationsOnboardingCompleted() } returns true
        coEvery { userRepo.notificationId } returns "12345"
        every { notificationsProvider.consentGiven() } returns false
        coEvery { userRepo.preferences?.notifications?.consentStatus } returns ConsentStatus.UNKNOWN

        runTest {
            notificationsRepo.login()

            verify(exactly = 1) { notificationsProvider.login("12345") }
            coVerify(exactly = 1) { userRepo.updateNotifications(ConsentStatus.DENIED) }
        }
    }

    @Test
    fun `Given login, when notifications onboarding completed, consent preference accepted and consent status any, then verify correct functions called`() {
        coEvery { notificationsDataStore.isNotificationsOnboardingCompleted() } returns true
        coEvery { userRepo.notificationId } returns "12345"
        every { notificationsProvider.consentGiven() } returns false
        coEvery { userRepo.preferences?.notifications?.consentStatus } returns ConsentStatus.ACCEPTED

        runTest {
            notificationsRepo.login()

            verify(exactly = 1) { notificationsProvider.login("12345") }
            coVerify(exactly = 0) { userRepo.updateNotifications(any()) }
        }
    }

    @Test
    fun `Given login, when notifications id and preferences are null, then verify correct functions are not called`() {
        coEvery { userRepo.notificationId } returns null
        coEvery { userRepo.preferences?.notifications?.consentStatus } returns null

        runTest {
            notificationsRepo.login()

            verify(exactly = 0) { notificationsProvider.login(any()) }
            coVerify(exactly = 0) { userRepo.updateNotifications(ConsentStatus.ACCEPTED) }
        }
    }

    @Test
    fun `Given logout, then call logout`() {
        runTest {
            notificationsRepo.logout()
            verify(exactly = 1) {
                notificationsProvider.logout()
            }
        }
    }

    @Test
    fun `Given request permission, then call request permission`() {
        runTest {
            notificationsRepo.requestPermission()
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
            coVerify { notificationsDataStore.firstPermissionRequestCompleted()}
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
