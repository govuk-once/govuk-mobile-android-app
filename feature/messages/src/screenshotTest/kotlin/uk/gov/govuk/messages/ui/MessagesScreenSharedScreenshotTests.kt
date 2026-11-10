package uk.gov.govuk.messages.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.android.tools.screenshot.PreviewTest

internal class MessagesScreenSharedScreenshotTests {
    @PreviewTest
    @PreviewLightDark
    @Composable
    fun MessagesScreenErrorScreenshots() {
        ErrorPreview()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun MessagesScreenNoInternetScreenshots() {
        NoInternetPreview()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun FooterScreenshots() {
        FooterPreview()
    }
}