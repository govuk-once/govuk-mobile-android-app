package uk.gov.govuk.dwp.linking.data

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response
import uk.gov.govuk.data.auth.AuthRepo
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.dwp.linking.remote.LinkingApi
import uk.gov.govuk.dwp.linking.remote.model.VerificationRequest
import uk.gov.govuk.dwp.linking.remote.model.VerificationResponse

class LinkingRepoTest {
    private val api = mockk<LinkingApi>()
    private val authRepo = mockk<AuthRepo>()
    private val repository = LinkingRepo(api, authRepo)

    @Test
    fun `Verification sends the access token through LinkingApi and returns its response`() = runTest {
        every { authRepo.getAccessToken() } returns "access-token"
        coEvery { api.getVerification(VerificationRequest("access-token")) } returns
            Response.success(VerificationResponse("verification-token"))
        val result = repository.getVerification()
        assertEquals("verification-token", (result as Result.Success).value.token)
        coVerify(exactly = 1) { api.getVerification(VerificationRequest("access-token")) }
        coVerify(exactly = 0) { authRepo.refreshTokens() }
    }

    @Test
    fun `Failed API verification is returned without refreshing credentials`() = runTest {
        every { authRepo.getAccessToken() } returns "access-token"
        coEvery { api.getVerification(any()) } throws java.io.IOException("Verification unavailable")
        assertTrue(repository.getVerification() is Result.Error)
        coVerify(exactly = 0) { authRepo.refreshTokens() }
    }

    @Test
    fun `Given expired credentials, then retry verification with the refreshed access token`() = runTest {
        every { authRepo.getAccessToken() } returnsMany listOf("expired", "refreshed")
        coEvery { api.getVerification(VerificationRequest("expired")) } returns Response.error(401, mockk(relaxed = true))
        coEvery { api.getVerification(VerificationRequest("refreshed")) } returns Response.success(VerificationResponse("verification"))
        coEvery { authRepo.refreshTokens() } returns true
        assertEquals("verification", (repository.getVerification() as Result.Success).value.token)
        coVerify(exactly = 1) { authRepo.refreshTokens() }
        coVerify(exactly = 1) { api.getVerification(VerificationRequest("expired")) }
        coVerify(exactly = 1) { api.getVerification(VerificationRequest("refreshed")) }
    }

    @Test
    fun `Given verification remains unauthorized after refresh, then stop after one retry`() = runTest {
        every { authRepo.getAccessToken() } returns "access-token"
        coEvery { api.getVerification(any()) } returns Response.error(401, mockk(relaxed = true))
        coEvery { authRepo.refreshTokens() } returns true
        assertTrue(repository.getVerification() is Result.AuthError)
        coVerify(exactly = 1) { authRepo.refreshTokens() }
        coVerify(exactly = 2) { api.getVerification(VerificationRequest("access-token")) }
    }
}
