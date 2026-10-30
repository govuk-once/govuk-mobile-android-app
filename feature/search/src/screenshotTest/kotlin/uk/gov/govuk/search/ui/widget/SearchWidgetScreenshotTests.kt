package uk.gov.govuk.search.ui.widget

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.android.tools.screenshot.PreviewTest
import uk.gov.govuk.search.ui.component.SearchHeaderPreview

internal class SearchWidgetScreenshotTests {
    @PreviewTest
    @PreviewLightDark
    @Composable
    fun SearchWidgetScreenshots() {
        SearchWidgetPreview()
    }
}