package uk.gov.govuk.data.flex.remote

import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response
import uk.gov.govuk.data.flex.FlexResult.Success
import uk.gov.govuk.data.flex.FlexResult.Error
import uk.gov.govuk.data.flex.model.FlexResponse

class ApiCallTest {
    private val apiCall = mockk<suspend () -> Response<FlexResponse>>(relaxed = true)
    private val response = mockk<Response<FlexResponse>>(relaxed = true)

    @Test
    fun `Given the response is successful, when the body is not null, then return success`() =
        runTest {
            coEvery { apiCall.invoke() } returns response
            every { response.isSuccessful } returns true
            every { response.body() } returns FlexResponse(userId = "12345")

            val result = safeFlexApiCall(apiCall)

            assertTrue(result is Success)
            assertEquals("12345", (result as Success).value.userId)
        }

    @Test
    fun `Given the response is successful, when the body is null, then return error`() = runTest {
        coEvery { apiCall.invoke() } returns response
        every { response.isSuccessful } returns true
        every { response.body() } returns null

        val result = safeFlexApiCall(apiCall)

        assertTrue(result is Error)
    }

    @Test
    fun `Given the response is unsuccessful, then return error`() = runTest {
        coEvery { apiCall.invoke() } returns response
        every { response.isSuccessful } returns false

        val result = safeFlexApiCall(apiCall)

        assertTrue(result is Error)
    }
}
