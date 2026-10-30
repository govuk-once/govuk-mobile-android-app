package uk.gov.govuk.travelalerts.ui.widget

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.android.tools.screenshot.PreviewTest
import uk.gov.govuk.travelalerts.ui.notificationsprompt.NotificationsRationaleScreenContentPreview

internal class TravelAlertsWidgetScreenshotTests {
    @PreviewTest
    @PreviewLightDark
    @Composable
    fun TravelAlertsLoadingScreenshots() {
        TravelAlertsLoadingPreview()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun TravelAlertsWidgetEmptyScreenshots() {
        TravelAlertsWidgetEmptyPreview()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun TravelAlertsWidgetLoadedScreenshots() {
        TravelAlertsWidgetLoadedPreview()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun TravelAlertsErrorScreenshots() {
        TravelAlertsErrorPreview()
    }
}