package uk.gov.govuk.visited.ui.widget

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest

internal class VisitedWidgetScreenshotTests {
    @PreviewTest
    @Preview(name = "NoVisitedItems", showBackground = true)
    @Composable
    fun NoVisitedItemsScreenshots() {
        NoVisitedItemsPreview()
    }

    @PreviewTest
    @Preview(name = "VisitedItems", showBackground = true)
    @Composable
    fun VisitedItemsScreenshots() {
        VisitedItemsPreview()
    }
}