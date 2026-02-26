package uk.gov.govuk.login.data

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import uk.gov.govuk.login.data.local.LoginDataStore

class LoginRepoTest {
    private val loginDataStore = mockk<LoginDataStore>(relaxed = true)

    @Test
    fun `Given the there is no refresh token expiry date stored, When get refresh token expiry date, then return null`() {
        val repo = LoginRepo(loginDataStore)

        coEvery { loginDataStore.getRefreshTokenExpiryDate() } returns null

        runTest {

            assertNull(repo.getRefreshTokenExpiryDate())
        }
    }

    @Test
    fun `Given the there is a refresh token expiry date stored, When get refresh token expiry date, then return expiry date`() {
        val repo = LoginRepo(loginDataStore)

        coEvery { loginDataStore.getRefreshTokenExpiryDate() } returns 12345L

        runTest {

            assertEquals(12345L, repo.getRefreshTokenExpiryDate())
        }
    }

    @Test
    fun `Given the there is no refresh token issued at date stored, When get refresh token issued at date, then return null`() {
        val repo = LoginRepo(loginDataStore)

        coEvery { loginDataStore.getRefreshTokenIssuedAtDate() } returns null

        runTest {

            assertNull(repo.getRefreshTokenIssuedAtDate())
        }
    }

    @Test
    fun `Given the there is a refresh token issued at date stored, When get refresh token issued at date, then return issued at date`() {
        val repo = LoginRepo(loginDataStore)

        coEvery { loginDataStore.getRefreshTokenIssuedAtDate() } returns 12345L

        runTest {

            assertEquals(12345L, repo.getRefreshTokenIssuedAtDate())
        }
    }

    @Test
    fun `Given a refresh token issued at date is 12345, When set refresh token issued at date, then update data store`() {
        val repo = LoginRepo(loginDataStore)

        runTest {
            repo.setRefreshTokenIssuedAtDate(12345L)

            coVerify { loginDataStore.setRefreshTokenIssuedAtDate(12345L) }
        }
    }

    @Test
    fun `Given a different user has logged in, When clear, then clear data store`() {
        val repo = LoginRepo(loginDataStore)

        runTest {
            repo.clear()

            coVerify { loginDataStore.clear() }
        }
    }
}
