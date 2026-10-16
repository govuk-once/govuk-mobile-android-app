package uk.gov.govuk.login.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest

internal class LoginScreenScreenshotTests {
    @PreviewTest
    @Preview(name = "LoginScreen", showBackground = true)
    @Composable
    fun LoginScreenPreviewScreenshots() {
        LoginScreenPreview()
    }
}