package uk.gov.govuk.dvla.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest

internal class InfoStatusItemScreenshotTests {
    @PreviewTest
    @Preview(name = "InfoStatusItem", showBackground = true)
    @Composable
    fun InfoStatusItemScreenshots() {
        InfoStatusItemPreview()
    }

    @PreviewTest
    @Preview(name = "InfoStatusItemWithSubtitle", showBackground = true)
    @Composable
    fun InfoStatusItemWithSubtitleScreenshots() {
        InfoStatusItemWithSubtitlePreview()
    }

    @PreviewTest
    @Preview(name = "InfoStatusItemWithNoIcon", showBackground = true)
    @Composable
    fun InfoStatusItemWithNoIconScreenshots() {
        InfoStatusItemWithNoIconPreview()
    }
}
