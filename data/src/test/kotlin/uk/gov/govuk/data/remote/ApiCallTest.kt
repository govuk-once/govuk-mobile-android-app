package uk.gov.govuk.data.remote

import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import net.bytebuddy.matcher.ElementMatchers.any
import org.junit.Assert
import org.junit.Test
import retrofit2.HttpException
import uk.gov.govuk.data.model.Result
import retrofit2.Response
import java.net.UnknownHostException

class ApiCallTest {
    private val apiCall = mockk<suspend () -> Response<Any>>(relaxed = true)
    private val response = mockk<Response<Any>>(relaxed = true)

    @Test
    fun `Given the response is successful, when the body is not null, then return success`() =
        runTest {
            coEvery { apiCall.invoke() } returns response
            every { response.isSuccessful } returns true
            every { response.body() } returns any<Object>()

            val result = safeApiCall(apiCall = apiCall)

            assertTrue(result is Result.Success)
        }

    @Test
    fun `Given the response is successful, when the body is null, then return error`() =
        runTest {
            coEvery { apiCall.invoke() } returns response
            every { response.isSuccessful } returns true
            every { response.body() } returns null

            val result = safeApiCall(apiCall = apiCall)

            assertTrue(result is Result.Error)
        }

    @Test
    fun `Given the response is unsuccessful, then return error`() = runTest {
        coEvery { apiCall.invoke() } returns response
        every { response.isSuccessful } returns false

        val result = safeApiCall(apiCall = apiCall)

        assertTrue(result is Result.Error)
    }

    @Test
    fun `Given the response is unsuccessful and retry is set, then retry`() = runTest {
        coEvery { apiCall.invoke() } returns response
        every { response.isSuccessful } returns false

        val result = safeApiCall(retry = { true }, apiCall = apiCall)

        assertTrue(result is Result.Error)
    }

    @Test
    fun `Returns device offline when exception is thrown`() = runTest {
        coEvery { apiCall.invoke() } throws UnknownHostException()

        val result = safeApiCall(apiCall = apiCall)

        Assert.assertTrue(result is Result.DeviceOffline)
    }

    @Test
    fun `Returns service not responding when exception is thrown`() = runTest {
        coEvery { apiCall.invoke() } throws HttpException(response)

        val result = safeApiCall(apiCall = apiCall)

        Assert.assertTrue(result is Result.ServiceNotResponding)
    }

    @Test
    fun `Returns error when exception is thrown`() = runTest {
        coEvery { apiCall.invoke() } throws Exception()

        val result = safeApiCall(apiCall = apiCall)

        Assert.assertTrue(result is Result.Error)
    }
}
