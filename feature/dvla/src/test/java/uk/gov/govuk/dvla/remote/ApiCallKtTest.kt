package uk.gov.govuk.dvla.remote

import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response
import uk.gov.govuk.data.auth.AuthRepo
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.dvla.domain.LicenceDetailsResult.Failure
import uk.gov.govuk.dvla.domain.LicenceDetailsResult.NotAvailableForEnquiry
import uk.gov.govuk.dvla.domain.LicenceDetailsResult.NotFound
import uk.gov.govuk.dvla.domain.LicenceDetailsResult.Success
import uk.gov.govuk.dvla.remote.model.LicenceResponse
import java.net.UnknownHostException

class ApiCallKtTest {
    private val apiCall = mockk<suspend () -> Response<LicenceResponse>>(relaxed = true)
    private val authRepo = mockk<AuthRepo>(relaxed = true)
    private val response = mockk<Response<LicenceResponse>>(relaxed = true)
    private val licenceResponse = mockk<LicenceResponse>(relaxed = true)

    private fun errorBodyOf(json: String) = json.toResponseBody("application/json".toMediaTypeOrNull())

    @Test
    fun `Returns success if body is not null`() = runTest {
        coEvery { apiCall.invoke() } returns response
        every { response.isSuccessful } returns true
        every { response.body() } returns licenceResponse

        val result = safeLicenceApiCall(apiCall, authRepo)

        assertTrue(result is Success)
    }

    @Test
    fun `Returns Failure wrapping Error if body is null`() = runTest {
        coEvery { apiCall.invoke() } returns response
        every { response.isSuccessful } returns true
        every { response.body() } returns null

        val result = safeLicenceApiCall(apiCall, authRepo)

        assertTrue(result is Failure)
        assertTrue((result as Failure).result is Result.Error)
    }

    @Test
    fun `Returns Failure wrapping AuthError for 401 after token refresh failure`() = runTest {
        coEvery { apiCall.invoke() } returns response
        every { response.code() } returns 401
        coEvery { authRepo.refreshTokens() } returns false

        val result = safeLicenceApiCall(apiCall, authRepo)

        assertTrue(result is Failure)
        assertTrue((result as Failure).result is Result.AuthError)
    }

    @Test
    fun `Retries API call for 401 after token refresh success`() = runTest {
        coEvery { apiCall.invoke() } returns response
        every { response.code() } returns 401 andThen 200
        every { response.isSuccessful } returns true
        every { response.body() } returns licenceResponse
        coEvery { authRepo.refreshTokens() } returns true

        val result = safeLicenceApiCall(apiCall, authRepo)

        assertTrue(result is Success)
    }

    @Test
    fun `Returns NotFound for 404 with code GUK-404-04`() = runTest {
        coEvery { apiCall.invoke() } returns response
        every { response.isSuccessful } returns false
        every { response.body() } returns null
        every { response.code() } returns 404
        every { response.errorBody() } returns errorBodyOf(
            """{"code":"GUK-404-04","message":"Driving Licence not found"}"""
        )

        val result = safeLicenceApiCall(apiCall, authRepo)

        assertEquals(NotFound, result)
    }

    @Test
    fun `Returns NotAvailableForEnquiry for 404 with code GUK-404-05`() = runTest {
        coEvery { apiCall.invoke() } returns response
        every { response.isSuccessful } returns false
        every { response.body() } returns null
        every { response.code() } returns 404
        every { response.errorBody() } returns errorBodyOf(
            """{"code":"GUK-404-05","message":"Driving licence not available for enquiry"}"""
        )

        val result = safeLicenceApiCall(apiCall, authRepo)

        assertEquals(NotAvailableForEnquiry, result)
    }

    @Test
    fun `Returns Failure wrapping ServiceNotResponding for 404 with an unrecognised code`() = runTest {
        coEvery { apiCall.invoke() } returns response
        every { response.isSuccessful } returns false
        every { response.body() } returns null
        every { response.code() } returns 404
        every { response.errorBody() } returns errorBodyOf(
            """{"code":"GUK-404-99","message":"Something else"}"""
        )

        val result = safeLicenceApiCall(apiCall, authRepo)

        assertTrue(result is Failure)
        val failureResult = (result as Failure).result
        assertTrue(failureResult is Result.ServiceNotResponding)
        assertEquals(404, (failureResult as Result.ServiceNotResponding).code)
    }

    @Test
    fun `Returns Failure wrapping ServiceNotResponding for any other status code`() = runTest {
        coEvery { apiCall.invoke() } returns response
        every { response.isSuccessful } returns false
        every { response.body() } returns null
        every { response.code() } returns 500

        val result = safeLicenceApiCall(apiCall, authRepo)

        assertTrue(result is Failure)
        val failureResult = (result as Failure).result
        assertTrue(failureResult is Result.ServiceNotResponding)
        assertEquals(500, (failureResult as Result.ServiceNotResponding).code)
    }

    @Test
    fun `Returns Failure wrapping DeviceOffline when exception is thrown`() = runTest {
        coEvery { apiCall.invoke() } throws UnknownHostException()

        val result = safeLicenceApiCall(apiCall, authRepo)

        assertTrue(result is Failure)
        assertTrue((result as Failure).result is Result.DeviceOffline)
    }
}
