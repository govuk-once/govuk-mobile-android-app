package uk.gov.govuk.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest

internal class RecommendUpdateScreenScreenshotTests {
    @PreviewTest
    @Preview(name = "RecommendUpdateScreen", showBackground = true)
    @Composable
    fun RecommendUpdatePreviewScreenshots() {
        RecommendUpdatePreview()
    }
}