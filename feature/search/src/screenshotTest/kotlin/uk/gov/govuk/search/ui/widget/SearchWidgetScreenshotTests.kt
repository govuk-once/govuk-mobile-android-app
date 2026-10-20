package uk.gov.govuk.search.ui.widget

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import uk.gov.govuk.search.ui.component.SearchHeaderPreview

internal class SearchWidgetScreenshotTests {
    @PreviewTest
    @Preview(name = "SearchWidget", showBackground = true)
    @Composable
    fun SearchWidgetScreenshots() {
        SearchWidgetPreview()
    }
}