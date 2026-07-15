package uk.gov.govuk.dvla.linking.data

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import uk.gov.govuk.data.auth.AuthRepo
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.dvla.linking.remote.LinkingApi
import uk.gov.govuk.dvla.linking.remote.model.VerificationRequest
import uk.gov.govuk.dvla.linking.remote.model.VerificationResponse
import kotlin.Exception

class LinkingRepoTest {
    private val api = mockk<LinkingApi>()
    private val authRepo = mockk<AuthRepo>()
    private lateinit var repo: LinkingRepo

    @Before
    fun setup() {
        repo = LinkingRepo(api, authRepo)
    }

    @Test
    fun `Given getVerification is called, when response is successful, then returns success`() =
        runTest {
            coEvery { api.getVerification(VerificationRequest("1234")) } returns Response.success(
                VerificationResponse("4321")
            )
            coEvery { authRepo.getAccessToken() } returns "1234"

            val result = repo.getVerification()

            assertTrue(result is Result.Success)
            coVerify(exactly = 1) { api.getVerification(VerificationRequest("1234")) }
        }

    @Test
    fun `Given getVerification is called, when an exception is thrown, then return error`() =
        runTest {
            coEvery { api.getVerification(VerificationRequest("1234")) } throws Exception("Exception")
            coEvery { authRepo.getAccessToken() } returns "1234"

            val result = repo.getVerification()

            assertTrue(result is Result.Error)
            coVerify(exactly = 1) { api.getVerification(VerificationRequest("1234")) }
        }
}
