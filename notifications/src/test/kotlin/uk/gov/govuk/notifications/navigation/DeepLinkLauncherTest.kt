package uk.gov.govuk.notifications.navigation

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Test

class DeepLinkLauncherTest {

    private val context = mockk<Context>(relaxed = true)
    private val packageManager = mockk<PackageManager>()
    private val mockIntent = mockk<Intent>(relaxed = true)
    private lateinit var deepLinkLauncher: DeepLinkLauncher

    @Before
    fun setup() {
        deepLinkLauncher = DeepLinkLauncher(context)

        io.mockk.mockkStatic("android.net.Uri")

        every { context.packageName } returns "uk.gov.govuk.app"
        every { context.packageManager } returns packageManager
        every { packageManager.getLaunchIntentForPackage("uk.gov.govuk.app") } returns mockIntent
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `Given a valid deep link, then the app is started with correct flags and data`() {
        val url = "https://gov.uk/home"
        val uriMock = mockk<Uri>()
        every { Uri.parse(url) } returns uriMock

        val intentSlot = slot<Intent>()
        every { context.startActivity(capture(intentSlot)) } returns Unit

        deepLinkLauncher.launchDeepLink(url)

        verify(exactly = 1) {
            mockIntent.data = uriMock
            mockIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(mockIntent)
        }
    }

    @Test
    fun `Given a blank deep link, then nothing is launched`() {
        deepLinkLauncher.launchDeepLink("   ")

        verify(exactly = 0) {
            context.startActivity(any())
        }
    }

    @Test
    fun `Given the package manager cannot find the app (returns null), then nothing is launched`() {
        every { packageManager.getLaunchIntentForPackage(any()) } returns null

        deepLinkLauncher.launchDeepLink("https://gov.uk")

        verify(exactly = 0) {
            context.startActivity(any())
        }
    }
}