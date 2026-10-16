package uk.gov.govuk.topics.ui.widget

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest

internal class TopicsWidgetScreenshotTests {
    @PreviewTest
    @Preview(name = "TopicsWidget", showBackground = true)
    @Composable
    fun TopicsWidgetScreenshots() {
        TopicsWidgetPreview()
    }

    @PreviewTest
    @Preview(name = "TopicsWidgetEmptyTopics", showBackground = true)
    @Composable
    fun TopicsWidgetEmptyTopicsScreenshots() {
        TopicsWidgetEmptyTopicsPreview()
    }
}