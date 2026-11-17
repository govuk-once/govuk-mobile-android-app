package uk.govuk.app.local.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.android.tools.screenshot.PreviewTest

internal class LocalConfirmationScreenScreenshotTests {
    @PreviewTest
    @PreviewLightDark
    @Composable
    fun UnitaryAuthorityLocalConfirmationScreenshots() {
        UnitaryAuthorityLocalConfirmationPreview()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun TwoTierAuthorityLocalConfirmationScreenshots() {
        TwoTierAuthorityLocalConfirmationPreview()
    }
}