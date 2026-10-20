package uk.gov.govuk.design.ui.component.error

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest

internal class ErrorScreenshotTests {
    @PreviewTest
    @Preview(name = "Error", showBackground = true)
    @Composable
    fun ErrorScreenshots() {
        MessagePreview()
    }
}