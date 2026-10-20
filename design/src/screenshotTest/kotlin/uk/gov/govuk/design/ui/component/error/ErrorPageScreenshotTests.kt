package uk.gov.govuk.design.ui.component.error

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest

internal class ErrorPageScreenshotTests {
    @PreviewTest
    @Preview(name = "ErrorPageWithoutAdditionalContent", showBackground = true)
    @Composable
    fun ErrorPageWithoutAdditionalContentScreenshots() {
        ErrorPageWithoutAdditionalContentPreview()
    }

    @PreviewTest
    @Preview(name = "ErrorPageWithAdditionalContent", showBackground = true)
    @Composable
    fun ErrorPageWithAdditionalContentScreenshots() {
        ErrorPageWithAdditionalContentPreview()
    }
}