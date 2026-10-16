package uk.gov.govuk.search.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest

internal class TextFieldScreenshotTests {
    @PreviewTest
    @Preview(name = "SearchField", showBackground = true)
    @Composable
    fun SearchHeaderScreenshots() {
        SearchHeaderPreview()
    }
}