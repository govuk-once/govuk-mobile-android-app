package uk.gov.govuk.topics.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest

internal class TopicScreenScreenshotTests {

    @PreviewTest
    @Preview(name = "ErrorScreenOffline", showBackground = true)
    @Composable
    fun ErrorScreenOfflineScreenshots() {
        ErrorScreenOfflinePreview()
    }

    @PreviewTest
    @Preview(name = "ErrorScreenProblem", showBackground = true)
    @Composable
    fun ErrorScreenProblemScreenshots() {
        ErrorScreenProblemPreview()
    }

    @PreviewTest
    @Preview(name = "TopicLoadingScreen", showBackground = true)
    @Composable
    fun TopicLoadingScreenScreenshots() {
        TopicLoadingScreenPreview()
    }
}