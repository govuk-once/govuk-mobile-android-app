package uk.gov.govuk.data.remote

import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.data.model.Result.AuthError
import uk.gov.govuk.data.model.Result.DeviceOffline
import uk.gov.govuk.data.model.Result.ServiceNotResponding
import java.net.UnknownHostException

class SafeApiCallTest {

    @Test
    fun `Given a 200 response with a body, then return Success containing the body`() = runTest {
        val body = "response body"
        val response = mockk<Response<String>> {
            every { isSuccessful } returns true
            every { code() } returns 200
            every { body() } returns body
        }

        val result = safeApiCall { response }

        assertTrue(result is Result.Success)
        assertEquals(body, (result as Result.Success).value)
    }

    @Test
    fun `Given a 200 response with null body, then return Error`() = runTest {
        val response = mockk<Response<String>> {
            every { isSuccessful } returns true
            every { code() } returns 200
            every { body() } returns null
        }

        val result = safeApiCall { response }

        assertTrue(result is Result.Error)
    }

    @Test
    fun `Given a 204 No Content response, then return Success containing Unit`() = runTest {
        val response = mockk<Response<Unit>> {
            every { isSuccessful } returns true
            every { code() } returns 204
            every { body() } returns null
        }

        val result = safeApiCall { response }

        assertTrue(result is Result.Success)
        assertEquals(Unit, (result as Result.Success).value)
    }

    @Test
    fun `Given an unsuccessful response, then return Error`() = runTest {
        val response = mockk<Response<String>> {
            every { isSuccessful } returns false
            every { code() } returns 401
        }

        val result = safeApiCall { response }

        assertTrue(result is Result.Error)
    }

    @Test
    fun `Given an Authentication exception, then return AuthError`() = runTest {
        val result = safeApiCall<String> { throw AuthenticationException() }

        assertTrue(result is AuthError)
    }

    @Test
    fun `Given a Unknown Host exception, then return DeviceOffline`() = runTest {
        val result = safeApiCall<String> { throw UnknownHostException("Unknown Host") }

        assertTrue(result is DeviceOffline)
    }

    @Test
    fun `Given a Http exception, then return ServiceNotResponding`() = runTest {
        val errorResponse = mockk<Response<Any>> {
            every { code() } returns 502
            every { message() } returns "Bad Gateway"
        }

        val result = safeApiCall<String> { throw HttpException(errorResponse) }

        assertTrue(result is ServiceNotResponding)
    }

    @Test
    fun `Given a generic exception, then return Error`() = runTest {
        val result = safeApiCall<String> { throw Exception("exception") }

        assertTrue(result is Result.Error)
    }
}