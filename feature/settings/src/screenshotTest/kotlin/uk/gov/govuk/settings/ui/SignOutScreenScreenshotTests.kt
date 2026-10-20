package uk.gov.govuk.settings.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest

internal class SignOutScreenScreenshotTests {
    @PreviewTest
    @Preview(name = "SignOutScreen", showBackground = true)
    @Composable
    fun SignOutScreenScreenshots() {
        SignOutPreview()
    }
}