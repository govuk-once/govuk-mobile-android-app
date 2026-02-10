package uk.gov.govuk.data.user

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import uk.gov.govuk.data.auth.AuthRepo
import uk.gov.govuk.data.user.model.UpdateAnalyticsRequest
import uk.gov.govuk.data.user.model.UpdateNotificationsRequest
import uk.gov.govuk.data.user.model.UpdateUserDataResponse
import uk.gov.govuk.data.user.remote.UserApi

class UserRepoTest {

    private val userApi = mockk<UserApi>(relaxed = true)
    private val authRepo = mockk<AuthRepo>(relaxed = true)
    private val response = mockk<Response<UpdateUserDataResponse>>()

    private lateinit var userRepo: UserRepo

    @Before
    fun setup() {
        userRepo = UserRepo(userApi, authRepo)

        every { response.isSuccessful } returns false
        every { response.body() } returns null
    }

    @Test
    fun `Given get user info is called, then get user info is called on the api`() =
        runTest {
            userRepo.getUserInfo()

            coVerify(exactly = 1) { userApi.getUserInfo() }
        }

    @Test
    fun `Given update notifications is called, when consented, then update notifications is called on the api`() =
        runTest {
            userRepo.updateNotifications(true)

            coVerify(exactly = 1) { userApi.updateNotifications(UpdateNotificationsRequest(true)) }
        }

    @Test
    fun `Given update notifications is called, when not consented, then update notifications is called on the api`() =
        runTest {
            coEvery { userApi.updateNotifications(UpdateNotificationsRequest(false)) } returns response

            userRepo.updateNotifications(false)

            coVerify(exactly = 1) { userApi.updateNotifications(UpdateNotificationsRequest(false)) }
        }

    @Test
    fun `Given update analytics is called, when consented, then update analytics is called on the api`() =
        runTest {
            userRepo.updateAnalytics(true)

            coVerify(exactly = 1) { userApi.updateAnalytics(UpdateAnalyticsRequest(true)) }
        }

    @Test
    fun `Given update analytics is called, when not consented and response code is 402, then update analytics is called on the api and refresh tokens is not called`() =
        runTest {
            every { response.code() } returns 402
            coEvery { userApi.updateAnalytics(UpdateAnalyticsRequest(false)) } returns response

            userRepo.updateAnalytics(false)

            coVerify(exactly = 1) { userApi.updateAnalytics(UpdateAnalyticsRequest(false)) }
            coVerify(exactly = 0) { authRepo.refreshTokens() }
        }

    @Test
    fun `Given update analytics is called, when not consented and response code is 401, then update analytics is called on the api and refresh tokens is called`() =
        runTest {
            every { response.code() } returns 401
            coEvery { userApi.updateAnalytics(UpdateAnalyticsRequest(false)) } returns response

            userRepo.updateAnalytics(false)

            coVerify(exactly = 1) { userApi.updateAnalytics(UpdateAnalyticsRequest(false)) }
            coVerify(exactly = 1) { authRepo.refreshTokens() }
        }

    @Test
    fun `Given update analytics is called, when not consented and response code is 403, then update analytics is called on the api and refresh tokens is called`() =
        runTest {
            every { response.code() } returns 403
            coEvery { userApi.updateAnalytics(UpdateAnalyticsRequest(false)) } returns response

            userRepo.updateAnalytics(false)

            coVerify(exactly = 1) { userApi.updateAnalytics(UpdateAnalyticsRequest(false)) }
            coVerify(exactly = 1) { authRepo.refreshTokens() }
        }
}
