package uk.gov.govuk.visited.ui.widget

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.android.tools.screenshot.PreviewTest

internal class VisitedWidgetScreenshotTests {
    @PreviewTest
    @PreviewLightDark
    @Composable
    fun NoVisitedItemsScreenshots() {
        NoVisitedItemsPreview()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun VisitedItemsScreenshots() {
        VisitedItemsPreview()
    }
}