package uk.gov.govuk.design.ui.component.error

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.android.tools.screenshot.PreviewTest

internal class OfflineMessageScreenshotTests {
    @PreviewTest
    @PreviewLightDark
    @Composable
    fun OfflineMessageScreenshots() {
        OfflineMessagePreview()
    }
}