package uk.gov.govuk.data.auth

import android.text.TextUtils
import android.text.TextUtils.isEmpty
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import net.openid.appauth.TokenRequest
import net.openid.appauth.TokenResponse
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class TokenResponseMapperTest {
    private val mapper = TokenResponseMapper()

    @Before
    fun setup() {
        // TextUtils.isEmpty is used internally by the AppAuth library
        mockkStatic(TextUtils::class)
        every { TextUtils.isEmpty(any()) } answers {
            val str = it.invocation.args[0] as CharSequence?
            str.isNullOrEmpty()
        }
    }

    @After
    fun tearDown() {
        unmockkStatic(TextUtils::class)
    }

    @Test
    fun `Given a valid token response, when mapping, then return the tokens`() {
        val request = mockk<TokenRequest>(relaxed = true)
        val response = TokenResponse.Builder(request)
            .setAccessToken("access_token")
            .setIdToken("id_token")
            .setRefreshToken("refresh_token")
            .build()

        val result = mapper.map(response)

        assertEquals("access_token", result.accessToken)
        assertEquals("id_token", result.idToken)
        assertEquals("refresh_token", result.refreshToken)
    }

    @Test
    fun `Given token response with null values, when mapping, then return nulls`() {
        val request = mockk<TokenRequest>(relaxed = true)
        val response = TokenResponse.Builder(request)
            .setAccessToken(null)
            .setIdToken(null)
            .setRefreshToken(null)
            .build()

        val result = mapper.map(response)

        assertNull(result.accessToken)
        assertNull(result.idToken)
        assertNull(result.refreshToken)
    }

    @Test
    fun `Given a null token response, when mapping, then return nulls`() {
        val result = mapper.map(null)

        assertNull(result.accessToken)
        assertNull(result.idToken)
        assertNull(result.refreshToken)
    }
}
