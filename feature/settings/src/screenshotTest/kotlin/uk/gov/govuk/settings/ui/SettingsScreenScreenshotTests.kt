package uk.gov.govuk.settings.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.android.tools.screenshot.PreviewTest

internal class SettingsScreenScreenshotTests {
    @PreviewTest
    @PreviewLightDark
    @Composable
    fun ManageLoginScreenshots() {
        ManageLoginPreview()
    }
}