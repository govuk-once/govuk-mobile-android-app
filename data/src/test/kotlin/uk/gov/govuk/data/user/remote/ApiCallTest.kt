package uk.gov.govuk.data.user.remote

import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response
import uk.gov.govuk.data.user.UserApiResult.Success
import uk.gov.govuk.data.user.UserApiResult.Error
import uk.gov.govuk.data.user.model.UserApiResponse

class ApiCallTest {
    private val apiCall = mockk<suspend () -> Response<UserApiResponse>>(relaxed = true)
    private val response = mockk<Response<UserApiResponse>>(relaxed = true)

    @Test
    fun `Given the response is successful, when the body is not null, then return success`() =
        runTest {
            coEvery { apiCall.invoke() } returns response
            every { response.isSuccessful } returns true
            every { response.body() } returns UserApiResponse(notificationId = "12345")

            val result = safeUserApiCall(apiCall)

            assertTrue(result is Success)
            assertEquals("12345", (result as Success).value.notificationId)
        }

    @Test
    fun `Given the response is successful, when the body is null, then return error`() = runTest {
        coEvery { apiCall.invoke() } returns response
        every { response.isSuccessful } returns true
        every { response.body() } returns null

        val result = safeUserApiCall(apiCall)

        assertTrue(result is Error)
    }

    @Test
    fun `Given the response is unsuccessful, then return error`() = runTest {
        coEvery { apiCall.invoke() } returns response
        every { response.isSuccessful } returns false

        val result = safeUserApiCall(apiCall)

        assertTrue(result is Error)
    }
}
