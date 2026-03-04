package uk.gov.govuk.notifications.data

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import uk.gov.govuk.notifications.NotificationsProvider
import uk.gov.govuk.notifications.data.local.NotificationsDataStore

class NotificationsRepoTest {

    private val notificationsDataStore = mockk<NotificationsDataStore>(relaxed = true)
    private val notificationsProvider = mockk<NotificationsProvider>(relaxed = true)

    @Test
    fun `Given the user has not previously completed notifications onboarding, When is notifications onboarding completed, then return false`() {
        val repo = NotificationsRepo(notificationsDataStore, notificationsProvider)

        coEvery { notificationsDataStore.isNotificationsOnboardingCompleted() } returns false

        runTest {
            assertFalse(repo.isNotificationsOnboardingCompleted())
        }
    }

    @Test
    fun `Given the user has previously completed notifications onboarding, When is notifications onboarding completed, then return true`() {
        val repo = NotificationsRepo(notificationsDataStore, notificationsProvider)

        coEvery { notificationsDataStore.isNotificationsOnboardingCompleted()} returns true

        runTest {
            assertTrue(repo.isNotificationsOnboardingCompleted())
        }
    }

    @Test
    fun `Given the user has completed notifications onboarding, When notifications onboarding completed, then update data store`() {
        val repo = NotificationsRepo(notificationsDataStore, notificationsProvider)

        runTest {
            repo.firstPermissionRequestCompleted()
            coVerify { notificationsDataStore.firstPermissionRequestCompleted()}
        }
    }

    @Test
    fun `Given the user has not previously requested permission, When is first permission request completed, then return false`() {
        val repo = NotificationsRepo(notificationsDataStore, notificationsProvider)

        coEvery { notificationsDataStore.isFirstPermissionRequestCompleted() } returns false

        runTest {

            assertFalse(repo.isFirstPermissionRequestCompleted())
        }
    }

    @Test
    fun `Given the user has previously requested permission, When is first permission request completed, then return true`() {
        val repo = NotificationsRepo(notificationsDataStore, notificationsProvider)

        coEvery { notificationsDataStore.isFirstPermissionRequestCompleted() } returns true

        runTest {
            assertTrue(repo.isFirstPermissionRequestCompleted())
        }
    }

    @Test
    fun `Given the user has completed first permission request, When first permission request completed, then update data store`() {
        val repo = NotificationsRepo(notificationsDataStore, notificationsProvider)

        runTest {
            repo.firstPermissionRequestCompleted()

            coVerify { notificationsDataStore.firstPermissionRequestCompleted()}
        }
    }

    @Test
    fun `Given permission is granted, When checking permission granted, then return true`() {
        val repo = NotificationsRepo(notificationsDataStore, notificationsProvider)
        every { notificationsProvider.permissionGranted() } returns true

        assertTrue(repo.permissionGranted())
    }

    @Test
    fun `Given permission is not granted, When checking permission granted, then return false`() {
        val repo = NotificationsRepo(notificationsDataStore, notificationsProvider)
        every { notificationsProvider.permissionGranted() } returns false

        assertFalse(repo.permissionGranted())
    }

    @Test
    fun `Given consent is given, When checking consent given, then return true`() {
        val repo = NotificationsRepo(notificationsDataStore, notificationsProvider)
        every { notificationsProvider.consentGiven() } returns true

        assertTrue(repo.consentGiven())
    }

    @Test
    fun `Given consent is not given, When checking consent given, then return false`() {
        val repo = NotificationsRepo(notificationsDataStore, notificationsProvider)
        every { notificationsProvider.consentGiven() } returns false

        assertFalse(repo.consentGiven())
    }

    @Test
    fun `When removing consent, then call provider to remove consent`() {
        val repo = NotificationsRepo(notificationsDataStore, notificationsProvider)

        repo.removeConsent()

        verify(exactly = 1) { notificationsProvider.removeConsent() }
    }
}
