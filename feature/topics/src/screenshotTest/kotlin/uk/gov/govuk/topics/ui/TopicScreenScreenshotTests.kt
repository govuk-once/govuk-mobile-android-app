package uk.gov.govuk.topics.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.android.tools.screenshot.PreviewTest

internal class TopicScreenScreenshotTests {

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun ErrorScreenOfflineScreenshots() {
        ErrorScreenOfflinePreview()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun ErrorScreenProblemScreenshots() {
        ErrorScreenProblemPreview()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun TopicLoadingScreenScreenshots() {
        TopicLoadingScreenPreview()
    }
}