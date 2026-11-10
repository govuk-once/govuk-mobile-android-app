package uk.gov.govuk.dvla.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.android.tools.screenshot.PreviewTest

internal class VehicleSummaryCardScreenshotTests {
    @PreviewTest
    @PreviewLightDark
    @Composable
    fun VehicleHeaderScreenshots() {
        VehicleHeaderPreview()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun VehicleSummaryCardScreenshots() {
        VehicleSummaryCardPreview()
    }
}

