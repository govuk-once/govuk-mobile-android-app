package uk.gov.govuk.topics.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.android.tools.screenshot.PreviewTest
import uk.gov.govuk.topics.ui.TopicLoadingScreenPreview

internal class CardScreenshotTests {

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun TopicSelectionCardSelectedScreenshots() {
        TopicSelectionCardSelectedPreview()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun TopicSelectionCardUnselectedScreenshots() {
        TopicSelectionCardUnselectedPreview()
    }
}