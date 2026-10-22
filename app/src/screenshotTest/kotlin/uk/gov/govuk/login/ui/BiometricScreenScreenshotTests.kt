package uk.gov.govuk.login.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.android.tools.screenshot.PreviewTest

internal class BiometricScreenScreenshotTests {
    @PreviewTest
    @PreviewLightDark
    @Composable
    fun BiometricPreviewScreenshots() {
        BiometricPreview()
    }
}