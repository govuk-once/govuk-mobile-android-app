package uk.gov.govuk.login.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import uk.gov.govuk.ui.RecommendUpdatePreview

internal class BiometricSettingsScreenScreenshotTests {
    @PreviewTest
    @Preview(name = "BiometricSettingsScreen", showBackground = true)
    @Composable
    fun BiometricSettingsPreviewScreenshots() {
        BiometricSettingsPreview()
    }
}