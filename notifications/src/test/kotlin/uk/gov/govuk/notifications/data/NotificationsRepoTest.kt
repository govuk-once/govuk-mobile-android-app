package uk.gov.govuk.notifications.data

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import uk.gov.govuk.notifications.data.local.NotificationsDataStore

class NotificationsRepoTest {

    private val notificationsDataStore = mockk<NotificationsDataStore>(relaxed = true)

    @Test
    fun `Given the user has not previously completed notifications onboarding, When is notifications onboarding completed, then return false`() {
        val repo = NotificationsRepo(notificationsDataStore)

        coEvery { notificationsDataStore.isNotificationsOnboardingCompleted() } returns false

        runTest {
            assertFalse(repo.isNotificationsOnboardingCompleted())
        }
    }

    @Test
    fun `Given the user has previously completed notifications onboarding, When is notifications onboarding completed, then return true`() {
        val repo = NotificationsRepo(notificationsDataStore)

        coEvery { notificationsDataStore.isNotificationsOnboardingCompleted()} returns true

        runTest {
            assertTrue(repo.isNotificationsOnboardingCompleted())
        }
    }

    @Test
    fun `Given the user has completed notifications onboarding, When notifications onboarding completed, then update data store`() {
        val repo = NotificationsRepo(notificationsDataStore)

        runTest {
            notificationsDataStore.notificationsOnboardingCompleted()

            coVerify { repo.notificationsOnboardingCompleted() }
        }
    }

    @Test
    fun `Given the user has not previously requested permission, When is first permission request completed, then return false`() {
        val repo = NotificationsRepo(notificationsDataStore)

        coEvery { notificationsDataStore.isFirstPermissionRequestCompleted() } returns false

        runTest {

            assertFalse(repo.isFirstPermissionRequestCompleted())
        }
    }

    @Test
    fun `Given the user has previously requested permission, When is first permission request completed, then return true`() {
        val repo = NotificationsRepo(notificationsDataStore)

        coEvery { notificationsDataStore.isFirstPermissionRequestCompleted() } returns true

        runTest {
            assertTrue(repo.isFirstPermissionRequestCompleted())
        }
    }

    @Test
    fun `Given the user has completed first permission request, When first permission request completed, then update data store`() {
        val repo = NotificationsRepo(notificationsDataStore)

        runTest {
            repo.firstPermissionRequestCompleted()

            coVerify { notificationsDataStore.firstPermissionRequestCompleted()}
        }
    }
}
