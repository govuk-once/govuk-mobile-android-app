package uk.govuk.app.local.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest

internal class LocalAddressSelectScreenScreenshotTests {
    @PreviewTest
    @Preview(name = "LocalAddressSelectScreen", showBackground = true)
    @Composable
    fun LocalAddressSelectScreenScreenshots() {
        LocalAddressSelectScreenPreview()
    }
}