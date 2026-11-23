package uk.gov.govuk.chat.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Devices.PHONE
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.android.tools.screenshot.PreviewTest

internal class IntroMessagesScreenshotTests {
    @PreviewTest
    @PreviewLightDark
    @Composable
    fun MessageScreenshots() {
        MessagePreview()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun ExampleQuestionsScreenshots() {
        ExampleQuestionsPreview()
    }

    @PreviewTest
    @Preview(device = "$PHONE, orientation=landscape")
    @Composable
    fun ExampleQuestionsLandscapeScreenshots() {
        ExampleQuestionsLandscapePreview()
    }

    @PreviewTest
    @Preview(fontScale = 2f)
    @Composable
    fun ExampleQuestionsLargeFontScreenshots() {
        ExampleQuestionsLargeFontPreview()
    }
}
