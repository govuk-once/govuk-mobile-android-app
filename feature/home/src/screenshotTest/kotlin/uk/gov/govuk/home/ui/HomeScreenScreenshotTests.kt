package uk.gov.govuk.home.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest

internal class HomeScreenScreenshotTests {
    @PreviewTest
    @Preview(name = "HomeScreen", showBackground = true)
    @Composable
    fun HomeScreenScreenshots() {
        HomePreview()
    }
}