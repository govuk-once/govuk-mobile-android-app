package uk.govuk.app.local.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.android.tools.screenshot.PreviewTest

internal class LocalWidgetScreenshotTests {
    @PreviewTest
    @PreviewLightDark
    @Composable
    fun NoLocalAuthorityScreenshots() {
        NoLocalAuthorityPreview()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun UnitaryLocalAuthorityScreenshots() {
        UnitaryLocalAuthorityPreview()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun TwoTierLocalAuthorityScreenshots() {
        TwoTierLocalAuthorityPreview()
    }
}