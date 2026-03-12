package uk.gov.govuk.data.user

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import uk.gov.govuk.data.auth.AuthRepo
import uk.gov.govuk.data.user.model.ConsentStatus
import uk.gov.govuk.data.user.model.Notifications
import uk.gov.govuk.data.user.model.User
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
    fun `Given init is called, then get user info is called on the api`() =
        runTest {
            userRepo.initUser()

            coVerify { userApi.getUserInfo() }
        }

    @Test
    fun `Given init is called, when the api response is successful, then the correct values are set`() =
        runTest {
            coEvery { userApi.getUserInfo() } returns Response.success(
                User(Notifications(ConsentStatus.ACCEPTED, "12345"))
            )

            userRepo.initUser()

            coVerify(exactly = 1) { userApi.getUserInfo() }

            assertEquals("12345", userRepo.notifications?.notificationId)
            assertEquals(ConsentStatus.ACCEPTED, userRepo.notifications?.consentStatus)
        }

    @Test
    fun `Given init is called, when the api response is unsuccessful, then the correct values are set`() =
        runTest {
            coEvery { userApi.getUserInfo() } returns Response.error(
                500,
                "Error".toResponseBody(null)
            )

            userRepo.initUser()

            coVerify(exactly = 1) { userApi.getUserInfo() }

            assertNull(userRepo.notifications?.notificationId)
            assertNull(userRepo.notifications?.consentStatus)
        }

    @Test
    fun `Given update notifications is called, when consented, then update notifications is called on the api`() =
        runTest {
            userRepo.updateNotifications(ConsentStatus.ACCEPTED)

            coVerify {
                userApi.updateNotifications(UpdateNotificationsRequest(ConsentStatus.ACCEPTED))
            }
        }

    @Test
    fun `Given update notifications is called, when not consented, then update notifications is called on the api`() =
        runTest {
            userRepo.updateNotifications(ConsentStatus.DENIED)

            coVerify {
                userApi.updateNotifications(UpdateNotificationsRequest(ConsentStatus.DENIED))
            }
        }

    @Test
    fun `Given init user is not called, then properties are null`() =
        runTest {
            assertNull(userRepo.notifications)
        }
}
