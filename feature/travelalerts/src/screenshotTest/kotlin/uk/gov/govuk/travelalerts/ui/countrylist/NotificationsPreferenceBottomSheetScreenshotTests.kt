package uk.gov.govuk.travelalerts.ui.countrylist

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest

internal class NotificationsPreferenceBottomSheetScreenshotTests {
    @PreviewTest
    @Preview(name = "NotificationsPreferenceBottomSheetContent", showBackground = true)
    @Composable
    fun NotificationsPreferenceBottomSheetContentScreenshots() {
        NotificationsPreferenceBottomSheetContentPreview()
    }

    @PreviewTest
    @Preview(name = "NotificationsPreferenceBottomSheetContentLoading", showBackground = true)
    @Composable
    fun NotificationsPreferenceBottomSheetContentLoadingScreenshots() {
        NotificationsPreferenceBottomSheetContentLoadingPreview()
    }
}