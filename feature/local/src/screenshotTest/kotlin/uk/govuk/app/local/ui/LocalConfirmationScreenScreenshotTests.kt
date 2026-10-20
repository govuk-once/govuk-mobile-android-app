package uk.govuk.app.local.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest

internal class LocalConfirmationScreenScreenshotTests {
    @PreviewTest
    @Preview(name = "UnitaryAuthorityLocalConfirmation", showBackground = true)
    @Composable
    fun UnitaryAuthorityLocalConfirmationScreenshots() {
        UnitaryAuthorityLocalConfirmationPreview()
    }

    @PreviewTest
    @Preview(name = "TwoTierAuthorityLocalConfirmation", showBackground = true)
    @Composable
    fun TwoTierAuthorityLocalConfirmationScreenshots() {
        TwoTierAuthorityLocalConfirmationPreview()
    }
}