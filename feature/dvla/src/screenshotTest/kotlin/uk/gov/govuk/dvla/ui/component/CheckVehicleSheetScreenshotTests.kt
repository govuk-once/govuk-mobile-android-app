package uk.gov.govuk.dvla.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest

internal class CheckVehicleSheetScreenshotTests {
    @PreviewTest
    @Preview(name = "CheckVehicleSheetEmpty", showBackground = true)
    @Composable
    fun CheckVehicleSheetEmptyScreenshots() {
        CheckVehicleSheetEmptyPreview()
    }

    @PreviewTest
    @Preview(name = "CheckVehicleSheetTyping", showBackground = true)
    @Composable
    fun CheckVehicleSheetTypingScreenshots() {
        CheckVehicleSheetTypingPreview()
    }

    @PreviewTest
    @Preview(name = "CheckVehicleSheetComplete", showBackground = true)
    @Composable
    fun CheckVehicleSheetCompleteScreenshots() {
        CheckVehicleSheetCompletePreview()
    }

    @PreviewTest
    @Preview(name = "CheckVehicleSheetLoading", showBackground = true)
    @Composable
    fun CheckVehicleSheetLoadingScreenshots() {
        CheckVehicleSheetLoadingPreview()
    }

    @PreviewTest
    @Preview(name = "CheckVehicleSheetSearchUnavailable", showBackground = true)
    @Composable
    fun CheckVehicleSheetSearchUnavailableScreenshots() {
        CheckVehicleSheetSearchUnavailablePreview()
    }

    @PreviewTest
    @Preview(name = "CheckVehicleSheetNotFound", showBackground = true)
    @Composable
    fun CheckVehicleSheetNotFoundScreenshots() {
        CheckVehicleSheetNotFoundPreview()
    }
}

