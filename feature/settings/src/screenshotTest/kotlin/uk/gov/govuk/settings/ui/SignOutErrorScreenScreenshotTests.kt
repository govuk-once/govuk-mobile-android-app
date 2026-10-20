package uk.gov.govuk.settings.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest

internal class SignOutErrorScreenScreenshotTests {
    @PreviewTest
    @Preview(name = "SignOutErrorScreen", showBackground = true)
    @Composable
    fun SignOutErrorScreenScreenshots() {
        SignOutErrorScreenPreview()
    }
}