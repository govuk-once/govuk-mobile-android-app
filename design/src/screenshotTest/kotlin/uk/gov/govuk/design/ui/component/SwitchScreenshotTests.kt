package uk.gov.govuk.design.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest

internal class SwitchScreenshotTests {
    @PreviewTest
    @Preview(name = "ToggleSwitchOn", showBackground = true)
    @Composable
    fun ToggleSwitchOnScreenshots() {
        ToggleSwitchOnPreview()
    }

    @PreviewTest
    @Preview(name = "ToggleSwitchOff", showBackground = true)
    @Composable
    fun ToggleSwitchOffScreenshots() {
        ToggleSwitchOffPreview()
    }
}