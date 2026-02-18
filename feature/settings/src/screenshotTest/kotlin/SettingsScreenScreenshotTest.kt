package uk.gov.govuk.settings.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import uk.gov.govuk.settings.R
import uk.gov.govuk.settings.SettingsUiState

@PreviewTest
@Preview(name = "settings_all_enabled", showBackground = true)
@Composable
internal fun PreviewSettingsAllEnabled() {
    SettingsScreenWrapper(
        state = SettingsUiState(
            userEmail = "test@example.com",
            isNotificationsEnabled = true,
            isAuthenticationEnabled = true,
            isAnalyticsEnabled = true
        ),
        notificationStatusResId = R.string.on_button
    )
}

@PreviewTest
@Preview(name = "settings_notifications_on_analytics_off", showBackground = true)
@Composable
internal fun PreviewSettingsNotificationsOnAnalyticsOff() {
    SettingsScreenWrapper(
        state = SettingsUiState(
            userEmail = "test@example.com",
            isNotificationsEnabled = true,
            isAuthenticationEnabled = true,
            isAnalyticsEnabled = false
        ),
        notificationStatusResId = R.string.off_button
    )
}