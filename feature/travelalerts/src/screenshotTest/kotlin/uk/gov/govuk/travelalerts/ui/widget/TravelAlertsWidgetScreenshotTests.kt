package uk.gov.govuk.travelalerts.ui.widget

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import uk.gov.govuk.travelalerts.ui.notificationsprompt.NotificationsRationaleScreenContentPreview

internal class TravelAlertsWidgetScreenshotTests {
    @PreviewTest
    @Preview(name = "TravelAlertsLoading", showBackground = true)
    @Composable
    fun TravelAlertsLoadingScreenshots() {
        TravelAlertsLoadingPreview()
    }

    @PreviewTest
    @Preview(name = "TravelAlertsWidgetEmpty", showBackground = true)
    @Composable
    fun TravelAlertsWidgetEmptyScreenshots() {
        TravelAlertsWidgetEmptyPreview()
    }

    @PreviewTest
    @Preview(name = "TravelAlertsWidgetLoaded", showBackground = true)
    @Composable
    fun TravelAlertsWidgetLoadedScreenshots() {
        TravelAlertsWidgetLoadedPreview()
    }

    @PreviewTest
    @Preview(name = "TravelAlertsError", showBackground = true)
    @Composable
    fun TravelAlertsErrorScreenshots() {
        TravelAlertsErrorPreview()
    }
}