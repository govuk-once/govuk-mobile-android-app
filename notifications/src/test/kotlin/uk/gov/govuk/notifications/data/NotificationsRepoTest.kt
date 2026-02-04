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
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.data.model.Result.Error
import uk.gov.govuk.data.model.Result.Success
import uk.gov.govuk.data.user.UserRepo
import uk.gov.govuk.data.user.model.GetUserInfoResponse
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
    fun `Given login is success, then return success with correct values`() {
        coEvery { userRepo.getUserInfo() } returns Result.Success(GetUserInfoResponse("12345"))

        runTest {
            val result = notificationsRepo.login()
            assert(result is Success)
            assertEquals("12345", (result as Success).value.notificationId)

            verify { notificationsProvider.login("12345") }
        }
    }

    @Test
    fun `Given give consent is called, then give consent is called on notifications provider and notifications consent is updated`() {
        runTest {
            notificationsRepo.giveConsent()
            verify(exactly = 1)  {
                notificationsProvider.giveConsent()
            }
            coVerify(exactly = 1)  {
                userRepo.updateNotifications(true)
            }
        }
    }


    @Test
    fun `Given remove consent is called, then remove consent is called on notifications provider and notifications consent is updated`() {
        runTest {
            notificationsRepo.removeConsent()
            verify(exactly = 1) {
                notificationsProvider.removeConsent()
            }
            coVerify(exactly = 1)  {
                userRepo.updateNotifications(false)
            }
        }
    }

    @Test
    fun `Given login is unsuccessful, then return error`() {
        coEvery { userRepo.getUserInfo() } returns Error()

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
