package uk.gov.govuk.notifications.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.android.tools.screenshot.PreviewTest

internal class NotificationsScreenScreenshotTests {
    @PreviewTest
    @PreviewLightDark
    @Composable
    fun NotificationsScreenScreenshots() {
        NotificationsScreenPreview()
    }
}