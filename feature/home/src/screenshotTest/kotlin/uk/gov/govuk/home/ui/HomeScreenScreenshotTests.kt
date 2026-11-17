package uk.gov.govuk.home.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.android.tools.screenshot.PreviewTest

internal class HomeScreenScreenshotTests {
    @PreviewTest
    @PreviewLightDark
    @Composable
    fun HomeScreenScreenshots() {
        HomePreview()
    }
}