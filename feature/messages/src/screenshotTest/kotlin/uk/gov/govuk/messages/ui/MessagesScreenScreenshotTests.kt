package uk.gov.govuk.messages.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.android.tools.screenshot.PreviewTest

internal class MessagesScreenScreenshotTests {
    @PreviewTest
    @PreviewLightDark
    @Composable
    fun MessagesLoadingScreenshots() {
        MessagesLoadingPreview()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun MessagesEmptyScreenshots() {
        MessagesEmptyPreview()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun MessagesLoadedScreenshots() {
        MessagesLoadedPreview()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun NotificationRowReadScreenshots() {
        NotificationRowReadPreview()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun NotificationRowUnreadScreenshots() {
        NotificationRowUnreadPreview()
    }
}