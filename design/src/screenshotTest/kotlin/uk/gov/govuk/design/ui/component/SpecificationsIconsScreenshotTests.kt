package uk.gov.govuk.design.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest

internal class SpecificationsIconsScreenshotTests {

    @PreviewTest
    @Preview(name = "HorizontalSpecificationsIcons", showBackground = true)
    @Composable
    fun HorizontalSpecificationsIconsScreenshots() {
        HorizontalSpecificationsIconsPreview()
    }

    @PreviewTest
    @Preview(name = "VerticalSpecificationsIcons", showBackground = true)
    @Composable
    fun VerticalSpecificationsIconsScreenshots() {
        VerticalSpecificationsIconsPreview()
    }
}