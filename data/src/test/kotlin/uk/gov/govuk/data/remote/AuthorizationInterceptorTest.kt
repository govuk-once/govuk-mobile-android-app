package uk.gov.govuk.data.remote

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import org.junit.Assert.assertEquals
import org.junit.Test
import uk.gov.govuk.data.auth.AuthRepo

class AuthorizationInterceptorTest {
    private val authRepo = mockk<AuthRepo>()
    private val interceptor = AuthorizationInterceptor(authRepo)
    private val chain = mockk<Interceptor.Chain>()
    private val request = Request.Builder()
        .url("https://api.gov.uk/test")
        .build()
    private val response = mockk<Response>()

    @Test
    fun `Given an access token, when intercepting, then add Authorization header`() {
        val accessToken = "valid_token"
        every { authRepo.getAccessToken() } returns accessToken
        every { chain.request() } returns request

        val requestSlot = slot<Request>()
        every { chain.proceed(capture(requestSlot)) } returns response

        val result = interceptor.intercept(chain)

        assertEquals(response, result)
        assertEquals("Bearer $accessToken", requestSlot.captured.header("Authorization"))
        verify(exactly = 1) { chain.proceed(any()) }
    }

    @Test
    fun `Given an empty access token, when intercepting, then add Bearer null header`() {
        val accessToken = ""
        every { authRepo.getAccessToken() } returns accessToken
        every { chain.request() } returns request

        val requestSlot = slot<Request>()
        every { chain.proceed(capture(requestSlot)) } returns response

        val result = interceptor.intercept(chain)

        assertEquals(response, result)
        assertEquals("Bearer", requestSlot.captured.header("Authorization"))
        verify(exactly = 1) { chain.proceed(any()) }
    }
}
