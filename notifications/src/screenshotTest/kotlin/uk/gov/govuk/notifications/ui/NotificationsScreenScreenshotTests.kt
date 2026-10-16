package uk.gov.govuk.notifications.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest

internal class NotificationsScreenScreenshotTests {
    @PreviewTest
    @Preview(name = "NotificationsScreen", showBackground = true)
    @Composable
    fun NotificationsScreenScreenshots() {
        NotificationsScreenPreview()
    }
}