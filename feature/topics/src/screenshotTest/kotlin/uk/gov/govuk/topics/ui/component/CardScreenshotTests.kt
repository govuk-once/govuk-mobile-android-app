package uk.gov.govuk.topics.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import uk.gov.govuk.topics.ui.TopicLoadingScreenPreview

internal class CardScreenshotTests {

    @PreviewTest
    @Preview(name = "TopicSelectionCardSelected", showBackground = true)
    @Composable
    fun TopicSelectionCardSelectedScreenshots() {
        TopicSelectionCardSelectedPreview()
    }

    @PreviewTest
    @Preview(name = "TopicSelectionCardUnselected", showBackground = true)
    @Composable
    fun TopicSelectionCardUnselectedScreenshots() {
        TopicSelectionCardUnselectedPreview()
    }
}