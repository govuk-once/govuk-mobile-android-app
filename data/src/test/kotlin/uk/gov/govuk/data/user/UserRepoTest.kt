package uk.gov.govuk.data.user

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import uk.gov.govuk.data.auth.AuthRepo
import uk.gov.govuk.data.user.model.Consent
import uk.gov.govuk.data.user.model.ConsentStatus
import uk.gov.govuk.data.user.model.User
import uk.gov.govuk.data.user.model.Preferences
import uk.gov.govuk.data.user.model.UpdateTermsAndConditionsRequest
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
                User(
                    "12345",
                    Preferences(
                        Consent(
                            ConsentStatus.ACCEPTED, "updated at"
                        )
                    )
                )
            )

            userRepo.initUser()

            assertEquals("12345", userRepo.notificationId)
            assertEquals(ConsentStatus.ACCEPTED, userRepo.preferences?.notifications?.consentStatus)
            assertEquals("updated at", userRepo.preferences?.notifications?.updatedAt)
        }

    @Test
    fun `Given update notifications is called, when consented, then update notifications is called on the api`() =
        runTest {
            userRepo.updateNotifications(ConsentStatus.ACCEPTED)

            coVerify { userApi.updateNotifications(UpdateNotificationsRequest(ConsentStatus.ACCEPTED)) }
        }

    @Test
    fun `Given update notifications is called, when not consented, then update notifications is called on the api`() =
        runTest {
            userRepo.updateNotifications(ConsentStatus.DENIED)

            coVerify { userApi.updateNotifications(UpdateNotificationsRequest(ConsentStatus.DENIED)) }
        }

    @Test
    fun `Given update terms and conditions is called, when consented, then update terms and conditions is called on the api`() =
        runTest {
            userRepo.updateTermsAndConditions(ConsentStatus.ACCEPTED)

            coVerify {
                userApi.updateTermsAndConditions(
                    UpdateTermsAndConditionsRequest(
                        ConsentStatus.ACCEPTED
                    )
                )
            }
        }

    @Test
    fun `Given update terms and conditions is called, when not consented, then update terms and conditions is called on the api`() =
        runTest {
            userRepo.updateTermsAndConditions(ConsentStatus.DENIED)

            coVerify {
                userApi.updateTermsAndConditions(
                    UpdateTermsAndConditionsRequest(
                        ConsentStatus.DENIED
                    )
                )
            }
        }

    @Test
    fun `Given no user init, when any user property is requested, then throw exception`() {
        val repo = UserRepoImpl(userApi, authRepo)
        val exception = assertThrows(IllegalStateException::class.java) {
            repo.notificationId
        }

        Assert.assertEquals("You must init user successfully before use!!!", exception.message)
    }
}
