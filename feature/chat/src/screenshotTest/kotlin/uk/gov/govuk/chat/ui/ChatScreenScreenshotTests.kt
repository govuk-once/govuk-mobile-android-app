package uk.gov.govuk.chat.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.android.tools.screenshot.PreviewTest

internal class ChatScreenScreenshotTests {
    @PreviewTest
    @PreviewLightDark
    @Composable
    fun ChatScreenScreenshots() {
        ChatScreenPreview()
    }
}