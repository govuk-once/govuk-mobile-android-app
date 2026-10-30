package uk.gov.govuk.design.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.android.tools.screenshot.PreviewTest

internal class SwitchScreenshotTests {
    @PreviewTest
    @PreviewLightDark
    @Composable
    fun ToggleSwitchOnScreenshots() {
        ToggleSwitchOnPreview()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun ToggleSwitchOffScreenshots() {
        ToggleSwitchOffPreview()
    }
}