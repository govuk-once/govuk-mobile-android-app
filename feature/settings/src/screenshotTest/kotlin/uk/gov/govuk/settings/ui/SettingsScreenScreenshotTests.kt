package uk.gov.govuk.settings.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest

internal class SettingsScreenScreenshotTests {
    @PreviewTest
    @Preview(name = "ManageLogin", showBackground = true)
    @Composable
    fun ManageLoginScreenshots() {
        ManageLoginPreview()
    }
}