package uk.gov.govuk.design.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest

internal class ScreenScreenshotTests {
    @PreviewTest
    @Preview(name = "BookendToWebScreen", showBackground = true)
    @Composable
    fun BookendToWebScreenScreenshots() {
        BookendToWebScreenPreview()
    }

    @PreviewTest
    @Preview(name = "BookendConnectingScreen", showBackground = true)
    @Composable
    fun BookendConnectingScreenScreenshots() {
        BookendConnectingScreenPreview()
    }

    @PreviewTest
    @Preview(name = "AccountConnectionSuccessScreen", showBackground = true)
    @Composable
    fun AccountConnectionSuccessScreenScreenshots() {
        AccountConnectionSuccessScreenPreview()
    }
}