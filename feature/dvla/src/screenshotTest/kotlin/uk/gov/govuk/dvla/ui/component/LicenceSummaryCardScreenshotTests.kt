package uk.gov.govuk.dvla.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest

internal class LicenceSummaryCardScreenshotTests {
    @PreviewTest
    @Preview(name = "LicenceHeader", showBackground = true)
    @Composable
    fun LicenceHeaderScreenshots() {
        LicenceHeaderPreview()
    }

    @PreviewTest
    @Preview(name = "LicenceSummaryCard", showBackground = true)
    @Composable
    fun LicenceSummaryCardScreenshots() {
        LicenceSummaryCardPreview()
    }

    @PreviewTest
    @Preview(name = "LicenceSummaryCardExpired", showBackground = true)
    @Composable
    fun LicenceSummaryCardExpiredScreenshots() {
        LicenceSummaryCardExpiredPreview()
    }
}
