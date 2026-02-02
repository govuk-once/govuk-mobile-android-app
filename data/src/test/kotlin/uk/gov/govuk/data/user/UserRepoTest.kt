package uk.gov.govuk.data.user

import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.data.user.remote.UserApi

class UserRepoTest {

    private val userApi = mockk<UserApi>(relaxed = true)

    private lateinit var userRepo: UserRepo

    @Before
    fun setup() {
        userRepo = UserRepo(userApi)
    }

    @Test
    fun `Given get user preferences is called, then get user preferences is called on the api`() =
        runTest {

            userRepo.getUserPreferences()

            coVerify { userApi.getUserPreferences() }
        }
}
