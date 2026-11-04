package uk.gov.govuk.travelalerts.ui.countrylist

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.android.tools.screenshot.PreviewTest

internal class NotificationsPreferenceBottomSheetScreenshotTests {
    @PreviewTest
    @PreviewLightDark
    @Composable
    fun NotificationsPreferenceBottomSheetContentScreenshots() {
        NotificationsPreferenceBottomSheetContentPreview()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun NotificationsPreferenceBottomSheetContentLoadingScreenshots() {
        NotificationsPreferenceBottomSheetContentLoadingPreview()
    }
}