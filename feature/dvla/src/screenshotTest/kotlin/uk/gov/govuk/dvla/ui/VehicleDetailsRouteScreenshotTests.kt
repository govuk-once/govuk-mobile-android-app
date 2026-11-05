package uk.gov.govuk.dvla.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.android.tools.screenshot.PreviewTest


internal class VehicleDetailsRouteScreenshotTests {
    @PreviewTest
    @PreviewLightDark
    @Composable
    fun VehicleDetailsScreenScreenshots() {
        VehicleDetailsScreenPreview()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun ErrorScreenScreenshots() {
        ErrorScreenPreview()
    }
}

