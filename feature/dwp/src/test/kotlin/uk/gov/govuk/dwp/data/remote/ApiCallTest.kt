package uk.gov.govuk.dwp.data.remote

import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import java.net.UnknownHostException
import kotlin.test.assertFailsWith
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response
import uk.gov.govuk.data.auth.AuthRepo
import uk.gov.govuk.dwp.data.remote.BenefitsResult.DeviceOffline
import uk.gov.govuk.dwp.data.remote.BenefitsResult.Error
import uk.gov.govuk.dwp.data.remote.BenefitsResult.NotFound
import uk.gov.govuk.dwp.data.remote.BenefitsResult.Success
import uk.gov.govuk.dwp.data.remote.model.CoreBioResponse

class ApiCallTest {
    private val apiCall = mockk<suspend () -> Response<CoreBioResponse>>(relaxed = true)
    private val authRepo = mockk<AuthRepo>(relaxed = true)
    private val response = mockk<Response<CoreBioResponse>>(relaxed = true)
    private val answer = mockk<CoreBioResponse>(relaxed = true)

    @Test
    fun `Returns success if body is not null`() = runTest {
        coEvery { apiCall.invoke() } returns response
        every { response.isSuccessful } returns true
        every { response.code() } returns 200
        every { response.body() } returns answer

        val result = safeBenefitsApiCall(apiCall, authRepo)

        assertTrue(result is Success)
        assertEquals(answer, (result as Success).value)
    }

    @Test
    fun `Returns error if body is null`() = runTest {
        coEvery { apiCall.invoke() } returns response
        every { response.isSuccessful } returns true
        every { response.code() } returns 200
        every { response.body() } returns null

        val result = safeBenefitsApiCall(apiCall, authRepo)

        assertTrue(result is Error)
    }

    @Test
    fun `Returns not found for 404`() = runTest {
        coEvery { apiCall.invoke() } returns response
        every { response.isSuccessful } returns false
        every { response.code() } returns 404

        val result = safeBenefitsApiCall(apiCall, authRepo)

        assertTrue(result is NotFound)
    }

    @Test
    fun `Returns error for any other status code`() = runTest {
        coEvery { apiCall.invoke() } returns response
        every { response.isSuccessful } returns false
        every { response.code() } returns 500

        val result = safeBenefitsApiCall(apiCall, authRepo)

        assertTrue(result is Error)
    }

    @Test
    fun `Returns device offline when exception is thrown`() = runTest {
        coEvery { apiCall.invoke() } throws UnknownHostException()

        val result = safeBenefitsApiCall(apiCall, authRepo)

        assertTrue(result is DeviceOffline)
    }

    @Test
    fun `Throws CancellationException when cancellation occurs`() = runTest {
        coEvery { apiCall.invoke() } throws CancellationException()

        assertFailsWith<CancellationException> {
            safeBenefitsApiCall(apiCall, authRepo)
        }
    }
}
