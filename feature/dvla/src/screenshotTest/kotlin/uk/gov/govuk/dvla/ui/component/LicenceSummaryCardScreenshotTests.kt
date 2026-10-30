package uk.gov.govuk.dvla.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.android.tools.screenshot.PreviewTest

internal class LicenceSummaryCardScreenshotTests {
    @PreviewTest
    @PreviewLightDark
    @Composable
    fun LicenceHeaderScreenshots() {
        LicenceHeaderPreview()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun LicenceSummaryCardScreenshots() {
        LicenceSummaryCardPreview()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun LicenceSummaryCardExpiredScreenshots() {
        LicenceSummaryCardExpiredPreview()
    }
}
