package uk.govuk.app.local.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest

internal class LocalExplainerScreenScreenshotTests {
    @PreviewTest
    @Preview(name = "LocalExplainerScreen", showBackground = true)
    @Composable
    fun LocalExplainerScreenScreenshots() {
        LocalScreenPreview()
    }
}