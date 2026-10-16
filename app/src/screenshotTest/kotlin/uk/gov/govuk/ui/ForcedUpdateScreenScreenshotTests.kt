package uk.gov.govuk.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest

internal class ForcedUpdateScreenScreenshotTests {
    @PreviewTest
    @Preview(name = "ForcedUpdateScreen", showBackground = true)
    @Composable
    fun ForcedUpdatePreviewScreenshots() {
        ForcedUpdatePreview()
    }
}