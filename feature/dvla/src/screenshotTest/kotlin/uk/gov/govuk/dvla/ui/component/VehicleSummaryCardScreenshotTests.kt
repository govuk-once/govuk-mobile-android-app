package uk.gov.govuk.dvla.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest

internal class VehicleSummaryCardScreenshotTests {
    @PreviewTest
    @Preview(name = "VehicleHeader", showBackground = true)
    @Composable
    fun VehicleHeaderScreenshots() {
        VehicleHeaderPreview()
    }

    @PreviewTest
    @Preview(name = "VehicleSummaryCard", showBackground = true)
    @Composable
    fun VehicleSummaryCardScreenshots() {
        VehicleSummaryCardPreview()
    }
}

