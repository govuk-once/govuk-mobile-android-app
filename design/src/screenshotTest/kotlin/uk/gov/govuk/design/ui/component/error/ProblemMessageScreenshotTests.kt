package uk.gov.govuk.design.ui.component.error

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest

internal class ProblemMessageScreenshotTests {
    @PreviewTest
    @Preview(name = "ProblemMessage", showBackground = true)
    @Composable
    fun ProblemMessageScreenshots() {
        ProblemMessagePreview()
    }
}