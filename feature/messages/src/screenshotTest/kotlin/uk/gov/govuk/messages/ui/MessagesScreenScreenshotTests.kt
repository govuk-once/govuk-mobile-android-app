package uk.gov.govuk.messages.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest

internal class MessagesScreenScreenshotTests {
    @PreviewTest
    @Preview(name = "MessagesLoading", showBackground = true)
    @Composable
    fun MessagesLoadingScreenshots() {
        MessagesLoadingPreview()
    }

    @PreviewTest
    @Preview(name = "MessagesEmpty", showBackground = true)
    @Composable
    fun MessagesEmptyScreenshots() {
        MessagesEmptyPreview()
    }

    @PreviewTest
    @Preview(name = "MessagesLoaded", showBackground = true)
    @Composable
    fun MessagesLoadedScreenshots() {
        MessagesLoadedPreview()
    }

    @PreviewTest
    @Preview(name = "NotificationRowRead", showBackground = true)
    @Composable
    fun NotificationRowReadScreenshots() {
        NotificationRowReadPreview()
    }

    @PreviewTest
    @Preview(name = "NotificationRowUnread", showBackground = true)
    @Composable
    fun NotificationRowUnreadScreenshots() {
        NotificationRowUnreadPreview()
    }
}