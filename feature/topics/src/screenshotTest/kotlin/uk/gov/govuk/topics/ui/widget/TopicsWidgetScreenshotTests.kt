package uk.gov.govuk.topics.ui.widget

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.android.tools.screenshot.PreviewTest

internal class TopicsWidgetScreenshotTests {
    @PreviewTest
    @PreviewLightDark
    @Composable
    fun TopicsWidgetScreenshots() {
        TopicsWidgetPreview()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun TopicsWidgetEmptyTopicsScreenshots() {
        TopicsWidgetEmptyTopicsPreview()
    }
}