package uk.gov.govuk.dvla.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.android.tools.screenshot.PreviewTest

internal class CheckVehicleSheetScreenshotTests {
    @PreviewTest
    @PreviewLightDark
    @Composable
    fun CheckVehicleSheetEmptyScreenshots() {
        CheckVehicleSheetEmptyPreview()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun CheckVehicleSheetTypingScreenshots() {
        CheckVehicleSheetTypingPreview()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun CheckVehicleSheetCompleteScreenshots() {
        CheckVehicleSheetCompletePreview()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun CheckVehicleSheetLoadingScreenshots() {
        CheckVehicleSheetLoadingPreview()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun CheckVehicleSheetSearchUnavailableScreenshots() {
        CheckVehicleSheetSearchUnavailablePreview()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun CheckVehicleSheetNotFoundScreenshots() {
        CheckVehicleSheetNotFoundPreview()
    }
}

