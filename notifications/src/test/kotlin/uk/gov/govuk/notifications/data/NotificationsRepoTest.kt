package uk.gov.govuk.notifications.data

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.data.flex.FlexRepo
import uk.gov.govuk.data.flex.FlexResult.Error
import uk.gov.govuk.data.flex.FlexResult.Success
import uk.gov.govuk.data.flex.model.FlexResponse
import uk.gov.govuk.notifications.NotificationsProvider
import uk.gov.govuk.notifications.data.local.NotificationsDataStore

class NotificationsRepoTest {
    private val notificationsDataStore = mockk<NotificationsDataStore>(relaxed = true)
    private val notificationsProvider = mockk<NotificationsProvider>(relaxed = true)
    private val flexRepo = mockk<FlexRepo>(relaxed = true)

    private lateinit var notificationsRepo: NotificationsRepo

    @Before
    fun setup() {
        notificationsRepo = NotificationsRepo(
            notificationsDataStore,
            notificationsProvider,
            flexRepo
        )
    }

    @Test
    fun `Given login is success, then return success with correct values`() {
        coEvery { flexRepo.getFlexPreferences() } returns Success(FlexResponse("12345"))

        runTest {
            val result = notificationsRepo.login()
            assert(result is Success)
            assertEquals("12345", (result as Success).value.userId)

            verify { notificationsProvider.login("12345") }
        }
    }

    @Test
    fun `Given login is unsuccessful, then return error`() {
        coEvery { flexRepo.getFlexPreferences() } returns Error()

        runTest {
            val result = notificationsRepo.login()
            assert(result is Error)
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

            coVerify { notificationsRepo.notificationsOnboardingCompleted() }
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

            coVerify { notificationsDataStore.firstPermissionRequestCompleted() }
        }
    }
}
