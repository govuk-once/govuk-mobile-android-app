package uk.gov.govuk.dvla.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.android.tools.screenshot.PreviewTest

internal class InfoStatusItemScreenshotTests {
    @PreviewTest
    @PreviewLightDark
    @Composable
    fun InfoStatusItemScreenshots() {
        InfoStatusItemPreview()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun InfoStatusItemWithSubtitleScreenshots() {
        InfoStatusItemWithSubtitlePreview()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun InfoStatusItemWithNoIconScreenshots() {
        InfoStatusItemWithNoIconPreview()
    }
}
