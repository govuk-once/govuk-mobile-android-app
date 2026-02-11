package uk.gov.govuk.data.remote

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
import java.net.UnknownHostException

class AuthenticatedApiCallTest {
    private val apiCall = mockk<suspend () -> Response<String>>(relaxed = true)
    private val authRepo = mockk<AuthRepo>(relaxed = true)
    private val response = mockk<Response<String>>(relaxed = true)

    // withAuthRetry tests

    @Test
    fun `withAuthRetry - returns response for successful call`() = runTest {
        coEvery { apiCall.invoke() } returns response
        every { response.code() } returns 200

        val result = withAuthRetry(apiCall, authRepo)

        assertEquals(response, result)
    }

    @Test
    fun `withAuthRetry - returns response for non-auth error codes`() = runTest {
        coEvery { apiCall.invoke() } returns response
        every { response.code() } returns 500

        val result = withAuthRetry(apiCall, authRepo)

        assertEquals(response, result)
    }

    @Test
    fun `withAuthRetry - retries on 401 after successful token refresh`() = runTest {
        coEvery { apiCall.invoke() } returns response
        every { response.code() } returns 401 andThen 200
        coEvery { authRepo.refreshTokens() } returns true

        withAuthRetry(apiCall, authRepo)

        coVerify(exactly = 2) { apiCall.invoke() }
    }

    @Test(expected = AuthenticationException::class)
    fun `withAuthRetry - throws AuthenticationException on 401 after failed token refresh`() = runTest {
        coEvery { apiCall.invoke() } returns response
        every { response.code() } returns 401
        coEvery { authRepo.refreshTokens() } returns false

        withAuthRetry(apiCall, authRepo)
    }

    @Test(expected = AuthenticationException::class)
    fun `withAuthRetry - throws AuthenticationException on 401 with retry disabled`() = runTest {
        coEvery { apiCall.invoke() } returns response
        every { response.code() } returns 401

        withAuthRetry(apiCall, authRepo, retry = false)
    }

    @Test
    fun `withAuthRetry - retries on 403 after successful token refresh`() = runTest {
        coEvery { apiCall.invoke() } returns response
        every { response.code() } returns 403 andThen 200
        coEvery { authRepo.refreshTokens() } returns true

        withAuthRetry(apiCall, authRepo)

        coVerify(exactly = 2) { apiCall.invoke() }
    }

    @Test(expected = AuthenticationException::class)
    fun `withAuthRetry - throws AuthenticationException on 403 after failed token refresh`() = runTest {
        coEvery { apiCall.invoke() } returns response
        every { response.code() } returns 403
        coEvery { authRepo.refreshTokens() } returns false

        withAuthRetry(apiCall, authRepo)
    }

    @Test(expected = UnknownHostException::class)
    fun `withAuthRetry - propagates exceptions`() = runTest {
        coEvery { apiCall.invoke() } throws UnknownHostException()

        withAuthRetry(apiCall, authRepo)
    }

    // safeAuthApiCall tests

    @Test
    fun `safeAuthApiCall - returns Success when response is successful with body`() = runTest {
        coEvery { apiCall.invoke() } returns response
        every { response.code() } returns 200
        every { response.isSuccessful } returns true
        every { response.body() } returns "data"

        val result = safeAuthApiCall(apiCall, authRepo)

        assertTrue(result is Result.Success)
        assertEquals("data", (result as Result.Success).value)
    }

    @Test
    fun `safeAuthApiCall - returns Error when response is successful with null body`() = runTest {
        coEvery { apiCall.invoke() } returns response
        every { response.code() } returns 200
        every { response.isSuccessful } returns true
        every { response.body() } returns null

        val result = safeAuthApiCall(apiCall, authRepo)

        assertTrue(result is Result.Error)
    }

    @Test
    fun `safeAuthApiCall - returns Error when response is not successful`() = runTest {
        coEvery { apiCall.invoke() } returns response
        every { response.code() } returns 500
        every { response.isSuccessful } returns false

        val result = safeAuthApiCall(apiCall, authRepo)

        assertTrue(result is Result.Error)
    }

    @Test
    fun `safeAuthApiCall - returns AuthError on 401 after failed token refresh`() = runTest {
        coEvery { apiCall.invoke() } returns response
        every { response.code() } returns 401
        coEvery { authRepo.refreshTokens() } returns false

        val result = safeAuthApiCall(apiCall, authRepo)

        assertTrue(result is Result.AuthError)
    }

    @Test
    fun `safeAuthApiCall - returns DeviceOffline on exception`() = runTest {
        coEvery { apiCall.invoke() } throws UnknownHostException()

        val result = safeAuthApiCall(apiCall, authRepo)

        assertTrue(result is Result.DeviceOffline)
    }
}
