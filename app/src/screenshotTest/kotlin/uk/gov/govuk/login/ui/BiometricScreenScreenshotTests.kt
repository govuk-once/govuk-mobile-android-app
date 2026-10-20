package uk.gov.govuk.login.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest

internal class BiometricScreenScreenshotTests {
    @PreviewTest
    @Preview(name = "BiometricScreen", showBackground = true)
    @Composable
    fun BiometricPreviewScreenshots() {
        BiometricPreview()
    }
}