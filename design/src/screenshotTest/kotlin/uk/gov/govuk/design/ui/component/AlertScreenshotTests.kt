package uk.gov.govuk.design.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest

internal class AlertScreenshotTests {
    @PreviewTest
    @Preview(name = "InfoAlert", showBackground = true)
    @Composable
    fun InfoAlertScreenshots() {
        InfoAlertPreview()
    }
}