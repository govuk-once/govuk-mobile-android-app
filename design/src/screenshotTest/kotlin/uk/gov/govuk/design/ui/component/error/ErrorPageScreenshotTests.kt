package uk.gov.govuk.design.ui.component.error

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark

internal class ErrorPageScreenshotTests {
    //@PreviewTest // Skipping this file to prevent out of memory loops on CI, fix later
    @PreviewLightDark
    @Composable
    fun ErrorPageWithoutAdditionalContentScreenshots() {
        ErrorPageWithoutAdditionalContentPreview()
    }

    //@PreviewTest // Skipping this file to prevent out of memory loops on CI, fix later
    @PreviewLightDark
    @Composable
    fun ErrorPageWithAdditionalContentScreenshots() {
        ErrorPageWithAdditionalContentPreview()
    }
}