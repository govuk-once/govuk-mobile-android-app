package uk.gov.govuk.messages.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.android.tools.screenshot.PreviewTest

internal class MessagesDetailScreenScreenshotTests {
    @PreviewTest
    @PreviewLightDark
    @Composable
    fun MessagesDetailLoadingScreenshots() {
        MessagesDetailLoadingPreview()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun MessagesDetailErrorScreenshots() {
        MessagesDetailErrorPreview()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun MessagesDetailLoadedScreenshots() {
        MessagesDetailLoadedPreview()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun ConfirmationDialogScreenshots() {
        ConfirmationDialogPreview()
    }
}