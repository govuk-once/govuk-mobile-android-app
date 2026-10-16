package uk.gov.govuk.dvla.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest

internal class SummaryErrorCardScreenshotTests {
    @PreviewTest
    @Preview(name = "SummaryErrorCard", showBackground = true)
    @Composable
    fun SummaryErrorCardScreenshots() {
        SummaryErrorCardPreview()
    }
}

