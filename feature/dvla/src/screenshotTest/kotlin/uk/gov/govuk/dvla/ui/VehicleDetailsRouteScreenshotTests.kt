package uk.gov.govuk.dvla.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest


internal class VehicleDetailsRouteScreenshotTests {
    @PreviewTest
    @Preview(name = "VehicleDetailsScreen", showBackground = true)
    @Composable
    fun VehicleDetailsScreenScreenshots() {
        VehicleDetailsScreenPreview()
    }

    @PreviewTest
    @Preview(name = "ErrorScreen", showBackground = true)
    @Composable
    fun ErrorScreenScreenshots() {
        ErrorScreenPreview()
    }
}

