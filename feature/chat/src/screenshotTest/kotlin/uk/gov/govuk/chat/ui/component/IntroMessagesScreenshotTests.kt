package uk.gov.govuk.chat.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.android.tools.screenshot.PreviewTest
import uk.gov.govuk.chat.ui.OnboardingPageOnePreview

internal class IntroMessagesScreenshotTests {
    @PreviewTest
    @PreviewLightDark
    @Composable
    fun MessageScreenshots() {
        MessagePreview()
    }
}