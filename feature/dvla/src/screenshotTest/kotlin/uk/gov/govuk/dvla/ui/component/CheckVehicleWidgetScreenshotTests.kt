package uk.gov.govuk.dvla.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest

internal class CheckVehicleWidgetScreenshotTests {
    @PreviewTest
    @Preview(name = "SearchVehicleCard", showBackground = true)
    @Composable
    fun SearchVehicleCardScreenshots() {
        SearchVehicleCardPreview()
    }
}

