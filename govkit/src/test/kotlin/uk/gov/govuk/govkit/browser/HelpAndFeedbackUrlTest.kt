package uk.gov.govuk.govkit.browser

import org.junit.Assert.assertEquals
import org.junit.Test
import java.net.URLEncoder

class HelpAndFeedbackUrlTest {

    private val deviceInfo = DeviceInfo(
        manufacturer = "Google",
        model = "Pixel 8",
        osVersion = "14"
    )

    @Test
    fun `getHelpAndFeedbackUrl uses the correct base URL`() {
        val url = getHelpAndFeedbackUrl("1.0.0", deviceInfo)
        assert(url.startsWith(Urls.HELP_AND_FEEDBACK_URL))
    }

    @Test
    fun `getHelpAndFeedbackUrl includes app version`() {
        val url = getHelpAndFeedbackUrl("1.2.3", deviceInfo)
        assert(url.contains("app_version=1.2.3"))
    }

    @Test
    fun `getHelpAndFeedbackUrl includes URL-encoded device info`() {
        val url = getHelpAndFeedbackUrl("1.0.0", deviceInfo)
        val encoded = URLEncoder.encode("Google Pixel 8 14", "UTF-8")
        assert(url.contains("phone=$encoded"))
    }

    @Test
    fun `getHelpAndFeedbackUrl builds correct full URL`() {
        val url = getHelpAndFeedbackUrl("1.0.0", deviceInfo)
        val encodedDevice = URLEncoder.encode("Google Pixel 8 14", "UTF-8")
        assertEquals("${Urls.HELP_AND_FEEDBACK_URL}?app_version=1.0.0&phone=$encodedDevice", url)
    }
}
