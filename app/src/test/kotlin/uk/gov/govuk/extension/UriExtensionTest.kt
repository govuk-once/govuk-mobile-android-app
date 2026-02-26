package uk.gov.govuk.extension

import android.net.Uri
import androidx.core.net.toUri
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class UriExtensionTest {
    private val uri = mockk<Uri>(relaxed = true)
    private val uriParam = mockk<Uri>(relaxed = true)

    private val allowedUrlParams = listOf("paramScheme://paramHost")

    @Before
    fun setup() {
        mockkStatic(Uri::class)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `Given a Uri, When getUrlParam() is called on it with allowed urls, should return a Uri of the url param`() {
        every { uriParam.scheme } returns "paramScheme"
        every { uriParam.host } returns "paramHost"
        every { uri.getQueryParameter("url")?.toUri() } returns uriParam

        runTest {
            assertEquals(uriParam, uri.getUrlParam(allowedUrlParams))
        }
    }

    @Test
    fun `Given a Uri, When getUrlParam() is called on it with a url param that is not in the allowed list, should return null`() {
        every { uriParam.scheme } returns "notAllowedParamScheme"
        every { uriParam.host } returns "notAllowedParamHost"
        every { uri.getQueryParameter("url")?.toUri() } returns uriParam

        runTest {
            assertNull(uri.getUrlParam(allowedUrlParams))
        }
    }

    @Test
    fun `Given a Uri, When getUrlParam() is called on it with a url that cannot be parsed, should return null`() {
        every { uri.getQueryParameter("url")?.toUri() } returns null

        runTest {
            assertNull(uri.getUrlParam(allowedUrlParams))
        }
    }

    @Test
    fun `Given a Uri, When getUrlParam() is called on it with no url param, should return null`() {
        every { uri.scheme } returns "scheme"
        every { uri.host } returns "host"
        every { uri.getQueryParameter("url") } returns null

        runTest {
            assertNull(uri.getUrlParam(allowedUrlParams))
        }
    }
}
