package uk.govuk.app.local.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest

internal class LocalLookupScreenScreenshotTests {
    @PreviewTest
    @Preview(name = "LocalLookupScreen", showBackground = true)
    @Composable
    fun LocalLookupScreenScreenshots() {
        LocalEntryScreenPreview()
    }
}