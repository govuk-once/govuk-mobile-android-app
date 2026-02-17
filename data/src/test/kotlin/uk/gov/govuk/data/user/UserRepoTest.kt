package uk.gov.govuk.data.user

import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.data.auth.AuthRepo
import uk.gov.govuk.data.user.model.UpdateTermsAndConditionsRequest
import uk.gov.govuk.data.user.model.UpdateNotificationsRequest
import uk.gov.govuk.data.user.remote.UserApi

class UserRepoTest {

    private val userApi = mockk<UserApi>(relaxed = true)
    private val authRepo = mockk<AuthRepo>(relaxed = true)

    private lateinit var userRepo: UserRepo

    @Before
    fun setup() {
        userRepo = UserRepo(userApi, authRepo)
    }

    @Test
    fun `Given get user info is called, then get user info is called on the api`() =
        runTest {
            userRepo.getUserInfo()

            coVerify { userApi.getUserInfo() }
        }

    @Test
    fun `Given update notifications is called, when consented, then update notifications is called on the api`() =
        runTest {
            userRepo.updateNotifications(true)

            coVerify { userApi.updateNotifications(UpdateNotificationsRequest(true)) }
        }

    @Test
    fun `Given update notifications is called, when not consented, then update notifications is called on the api`() =
        runTest {
            userRepo.updateNotifications(false)

            coVerify { userApi.updateNotifications(UpdateNotificationsRequest(false)) }
        }

    @Test
    fun `Given update terms and conditions is called, when consented, then update terms and conditions is called on the api`() =
        runTest {
            userRepo.updateTermsAndConditions(true)

            coVerify { userApi.updateTermsAndConditions(UpdateTermsAndConditionsRequest(true)) }
        }

    @Test
    fun `Given update terms and conditions is called, when not consented, then update terms and conditions is called on the api`() =
        runTest {
            userRepo.updateTermsAndConditions(false)

            coVerify { userApi.updateTermsAndConditions(UpdateTermsAndConditionsRequest(false)) }
        }
}
