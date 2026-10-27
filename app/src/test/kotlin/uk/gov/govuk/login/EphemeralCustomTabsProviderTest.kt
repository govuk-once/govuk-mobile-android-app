package uk.gov.govuk.login

import android.content.Context
import android.content.Intent
import androidx.browser.customtabs.CustomTabsClient
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class EphemeralCustomTabsProviderTest {

    private val context = mockk<Context>()
    private lateinit var provider: EphemeralCustomTabsProvider

    @Before
    fun setup() {
        mockkStatic(CustomTabsClient::class)
        provider = EphemeralCustomTabsProvider(context)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `Given no browser package is found, when isSupported is accessed, then returns false`() {
        every { CustomTabsClient.getPackageName(context, null) } returns null

        assertFalse(provider.isSupported)
    }

    @Test
    fun `Given a browser package is found and ephemeral browsing is supported, when isSupported is accessed, then returns true`() {
        val packageName = "com.android.chrome"
        every { CustomTabsClient.getPackageName(context, null) } returns packageName
        every { CustomTabsClient.isEphemeralBrowsingSupported(context, packageName) } returns true

        assertTrue(provider.isSupported)
    }

    @Test
    fun `Given a browser package is found but ephemeral browsing is not supported, when isSupported is accessed, then returns false`() {
        val packageName = "com.android.chrome"
        every { CustomTabsClient.getPackageName(context, null) } returns packageName
        every { CustomTabsClient.isEphemeralBrowsingSupported(context, packageName) } returns false

        assertFalse(provider.isSupported)
    }

    @Test
    fun `Given CustomTabsClient throws an exception, when isSupported is accessed, then returns false`() {
        every { CustomTabsClient.getPackageName(context, null) } throws RuntimeException("no browser")

        assertFalse(provider.isSupported)
    }

    @Test
    fun `Given an auth url and redirect uri, when fallbackIntent is called, then intent extras are set correctly`() {
        mockkConstructor(Intent::class)
        every { anyConstructed<Intent>().putExtra(any<String>(), any<String>()) } returns mockk(relaxed = true)

        provider.fallbackIntent("https://auth.gov.uk/login", "app://callback")

        verify { anyConstructed<Intent>().putExtra("auth_url", "https://auth.gov.uk/login") }
        verify { anyConstructed<Intent>().putExtra("redirect_uri", "app://callback") }
    }
}
