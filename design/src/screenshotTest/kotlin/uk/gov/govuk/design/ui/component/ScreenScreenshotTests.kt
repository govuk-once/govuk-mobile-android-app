package uk.gov.govuk.design.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.android.tools.screenshot.PreviewTest

internal class ScreenScreenshotTests {
    @PreviewTest
    @PreviewLightDark
    @Composable
    fun BookendToWebScreenScreenshots() {
        BookendToWebScreenPreview()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun BookendConnectingScreenScreenshots() {
        BookendConnectingScreenPreview()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun AccountConnectionSuccessScreenScreenshots() {
        AccountConnectionSuccessScreenPreview()
    }
}