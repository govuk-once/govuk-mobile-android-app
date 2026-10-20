package uk.gov.govuk.messages.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest

internal class MessagesScreenSharedScreenshotTests {
    @PreviewTest
    @Preview(name = "MessagesScreenError", showBackground = true)
    @Composable
    fun MessagesScreenErrorScreenshots() {
        ErrorPreview()
    }

    @PreviewTest
    @Preview(name = "MessagesScreenNoInternet", showBackground = true)
    @Composable
    fun MessagesScreenNoInternetScreenshots() {
        NoInternetPreview()
    }

    @PreviewTest
    @Preview(name = "Footer", showBackground = true)
    @Composable
    fun FooterScreenshots() {
        FooterPreview()
    }
}