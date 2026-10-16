package uk.gov.govuk.chat.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import uk.gov.govuk.chat.ui.OnboardingPageOnePreview

internal class IntroMessagesScreenshotTests {
    @PreviewTest
    @Preview(name = "Message", showBackground = true)
    @Composable
    fun MessageScreenshots() {
        MessagePreview()
    }
}