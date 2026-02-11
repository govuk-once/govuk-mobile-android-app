package uk.gov.govuk.chat.data.remote

import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response
import uk.gov.govuk.chat.data.remote.ChatResult.AuthError
import uk.gov.govuk.chat.data.remote.ChatResult.AwaitingAnswer
import uk.gov.govuk.chat.data.remote.ChatResult.DeviceOffline
import uk.gov.govuk.chat.data.remote.ChatResult.Error
import uk.gov.govuk.chat.data.remote.ChatResult.NotFound
import uk.gov.govuk.chat.data.remote.ChatResult.RateLimitExceeded
import uk.gov.govuk.chat.data.remote.ChatResult.Success
import uk.gov.govuk.chat.data.remote.ChatResult.ValidationError
import uk.gov.govuk.chat.data.remote.model.Answer
import uk.gov.govuk.data.auth.AuthRepo
import java.net.UnknownHostException
import kotlin.test.assertEquals

class ApiCallKtTest {
    private val apiCall = mockk<suspend () -> Response<Answer>>(relaxed = true)
    private val authRepo = mockk<AuthRepo>(relaxed = true)
    private val response = mockk<Response<Answer>>(relaxed = true)
    private val answer = mockk<Answer>(relaxed = true)

    @Test
    fun `Returns awaiting answer for 202`() = runTest {
        coEvery { apiCall.invoke() } returns response
        every { response.isSuccessful } returns true
        every { response.code() } returns 202

        val result = safeChatApiCall(apiCall, authRepo)

        assertTrue(result is AwaitingAnswer)
    }

    @Test
    fun `Returns success if body is not null`() = runTest {
        coEvery { apiCall.invoke() } returns response
        every { response.isSuccessful } returns true
        every { response.code() } returns 200
        every { response.body() } returns answer

        val result = safeChatApiCall(apiCall, authRepo)

        assertTrue(result is Success)
        assertEquals(answer, (result as Success).value)
    }

    @Test
    fun `Returns error if body is null`() = runTest {
        coEvery { apiCall.invoke() } returns response
        every { response.isSuccessful } returns true
        every { response.code() } returns 200
        every { response.body() } returns null

        val result = safeChatApiCall(apiCall, authRepo)

        assertTrue(result is Error)
    }

    @Test
    fun `Returns auth error for 401 after token refresh failure`() = runTest {
        coEvery { apiCall.invoke() } returns response
        every { response.code() } returns 401
        coEvery { authRepo.refreshTokens() } returns false

        val result = safeChatApiCall(apiCall, authRepo)

        assertTrue(result is AuthError)
    }

    @Test
    fun `Retries API call for 401 after token refresh success`() = runTest {
        coEvery { apiCall.invoke() } returns response
        every { response.code() } returns 401 andThen 200
        every { response.isSuccessful } returns true
        every { response.body() } returns answer
        coEvery { authRepo.refreshTokens() } returns true

        val result = safeChatApiCall(apiCall, authRepo)

        assertTrue(result is Success)
    }

    @Test
    fun `Returns auth error for 403 after token refresh failure`() = runTest {
        coEvery { apiCall.invoke() } returns response
        every { response.code() } returns 403
        coEvery { authRepo.refreshTokens() } returns false

        val result = safeChatApiCall(apiCall, authRepo)

        assertTrue(result is AuthError)
    }

    @Test
    fun `Retries API call for 403 after token refresh success`() = runTest {
        coEvery { apiCall.invoke() } returns response
        every { response.code() } returns 403 andThen 200
        every { response.isSuccessful } returns true
        every { response.body() } returns answer
        coEvery { authRepo.refreshTokens() } returns true

        val result = safeChatApiCall(apiCall, authRepo)

        assertTrue(result is Success)
    }

    @Test
    fun `Returns not found for 404`() = runTest {
        coEvery { apiCall.invoke() } returns response
        every { response.isSuccessful } returns false
        every { response.code() } returns 404

        val result = safeChatApiCall(apiCall, authRepo)

        assertTrue(result is NotFound)
    }

    @Test
    fun `Returns validation error for 422`() = runTest {
        coEvery { apiCall.invoke() } returns response
        every { response.isSuccessful } returns false
        every { response.code() } returns 422

        val result = safeChatApiCall(apiCall, authRepo)

        assertTrue(result is ValidationError)
    }

    @Test
    fun `Returns rate limit exceeded for 429`() = runTest {
        coEvery { apiCall.invoke() } returns response
        every { response.isSuccessful } returns false
        every { response.code() } returns 429

        val result = safeChatApiCall(apiCall, authRepo)

        assertTrue(result is RateLimitExceeded)
    }

    @Test
    fun `Returns error for any other status code`() = runTest {
        coEvery { apiCall.invoke() } returns response
        every { response.isSuccessful } returns false
        every { response.code() } returns 500

        val result = safeChatApiCall(apiCall, authRepo)

        assertTrue(result is Error)
    }

    @Test
    fun `Returns device offline when exception is thrown`() = runTest {
        coEvery { apiCall.invoke() } throws UnknownHostException()

        val result = safeChatApiCall(apiCall, authRepo)

        assertTrue(result is DeviceOffline)
    }
}
