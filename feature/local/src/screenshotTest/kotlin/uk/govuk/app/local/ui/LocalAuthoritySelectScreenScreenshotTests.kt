package uk.govuk.app.local.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest

internal class LocalAuthoritySelectScreenScreenshotTests {
    @PreviewTest
    @Preview(name = "LocalAuthoritySelectScreen", showBackground = true)
    @Composable
    fun LocalAuthoritySelectScreenScreenshots() {
        LocalAuthoritySelectScreenPreview()
    }
}