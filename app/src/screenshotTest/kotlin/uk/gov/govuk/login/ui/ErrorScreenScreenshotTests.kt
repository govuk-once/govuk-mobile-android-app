package uk.gov.govuk.login.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest

internal class ErrorScreenScreenshotTests {
    @PreviewTest
    @Preview(name = "ErrorScreen", showBackground = true)
    @Composable
    fun ErrorScreenPreviewScreenshots() {
        ErrorScreenPreview()
    }
}