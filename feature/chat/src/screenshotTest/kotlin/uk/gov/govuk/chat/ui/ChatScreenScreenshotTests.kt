package uk.gov.govuk.chat.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest

internal class ChatScreenScreenshotTests {
    @PreviewTest
    @Preview(name = "ChatScreen", showBackground = true)
    @Composable
    fun ChatScreenScreenshots() {
        ChatScreenPreview()
    }
}