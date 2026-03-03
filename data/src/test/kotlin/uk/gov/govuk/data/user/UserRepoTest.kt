package uk.gov.govuk.data.user

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import uk.gov.govuk.data.auth.AuthRepo
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.data.user.model.ConsentStatus
import uk.gov.govuk.data.user.model.Notifications
import uk.gov.govuk.data.user.model.User
import uk.gov.govuk.data.user.model.Preferences
import uk.gov.govuk.data.user.model.UpdateNotificationsRequest
import uk.gov.govuk.data.user.remote.UserApi

class UserRepoTest {

    private val userApi = mockk<UserApi>(relaxed = true)
    private val authRepo = mockk<AuthRepo>(relaxed = true)

    private lateinit var userRepo: UserRepo

    @Before
    fun setup() {
        userRepo = UserRepoImpl(userApi, authRepo)
    }

    @Test
    fun `Given init is called, when flex is enabled, then get user info is called on the api`() =
        runTest {
            userRepo.initUser(true)

            coVerify { userApi.getUserInfo() }
        }

    @Test
    fun `Given init is called, when flex is not enabled, then get user info is not called on the api`() =
        runTest {
            userRepo.initUser(false)

            coVerify(exactly = 0) { userApi.getUserInfo() }
        }

    @Test
    fun `Given init is called, when the api response is successful, then the correct values are set`() =
        runTest {
            coEvery { userApi.getUserInfo() } returns Response.success(
                User(
                    "12345",
                    Preferences(Notifications(ConsentStatus.ACCEPTED))
                )
            )

            userRepo.initUser(true)

            coVerify(exactly = 1) { userApi.getUserInfo() }

            assertEquals("12345", userRepo.notificationId)
            assertEquals(ConsentStatus.ACCEPTED, userRepo.preferences?.notifications?.consentStatus)
        }

    @Test
    fun `Given init is called, when flex is not enabled, then the correct values are set`() =
        runTest {
            userRepo.initUser(false)

            coVerify(exactly = 0) { userApi.getUserInfo() }

            assertNull(userRepo.notificationId)
            assertNull(userRepo.preferences?.notifications?.consentStatus)
        }

    @Test
    fun `Given update notifications is called, when consented, then update notifications is called on the api`() =
        runTest {
            userRepo.initUser(true)

            userRepo.updateNotifications(ConsentStatus.ACCEPTED)

            coVerify { userApi.updateNotifications(UpdateNotificationsRequest(Preferences(Notifications(ConsentStatus.ACCEPTED)))) }
        }

    @Test
    fun `Given update notifications is called, when not consented, then update notifications is called on the api`() =
        runTest {
            userRepo.initUser(true)

            userRepo.updateNotifications(ConsentStatus.DENIED)

            coVerify { userApi.updateNotifications(UpdateNotificationsRequest(Preferences(Notifications(ConsentStatus.DENIED)))) }
        }

    @Test
    fun `Given update notifications is called, when flex is not enabled, then return not sent`() =
        runTest {
            userRepo.initUser(false)

            val result = userRepo.updateNotifications(ConsentStatus.DENIED)
            assert(result is Result.NotSent)

            coVerify(exactly = 0) { userApi.updateNotifications(UpdateNotificationsRequest(Preferences(Notifications(ConsentStatus.DENIED)))) }
        }
}
