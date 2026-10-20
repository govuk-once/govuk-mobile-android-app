package uk.gov.govuk.messages.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest

internal class MessagesDetailScreenScreenshotTests {
    @PreviewTest
    @Preview(name = "MessagesDetailLoading", showBackground = true)
    @Composable
    fun MessagesDetailLoadingScreenshots() {
        MessagesDetailLoadingPreview()
    }

    @PreviewTest
    @Preview(name = "MessagesDetailError", showBackground = true)
    @Composable
    fun MessagesDetailErrorScreenshots() {
        MessagesDetailErrorPreview()
    }

    @PreviewTest
    @Preview(name = "MessagesDetailLoaded", showBackground = true)
    @Composable
    fun MessagesDetailLoadedScreenshots() {
        MessagesDetailLoadedPreview()
    }

    @PreviewTest
    @Preview(name = "ConfirmationDialog", showBackground = true)
    @Composable
    fun ConfirmationDialogScreenshots() {
        ConfirmationDialogPreview()
    }
}