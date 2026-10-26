package uk.gov.govuk.search.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.android.tools.screenshot.PreviewTest

internal class TextFieldScreenshotTests {
    @PreviewTest
    @PreviewLightDark
    @Composable
    fun SearchHeaderScreenshots() {
        SearchHeaderPreview()
    }
}