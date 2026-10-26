package uk.gov.govuk.design.ui.component.error

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.android.tools.screenshot.PreviewTest

internal class ErrorPageScreenshotTests {
    @PreviewTest
    @PreviewLightDark
    @Composable
    fun ErrorPageWithoutAdditionalContentScreenshots() {
        ErrorPageWithoutAdditionalContentPreview()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun ErrorPageWithAdditionalContentScreenshots() {
        ErrorPageWithAdditionalContentPreview()
    }
}