package uk.gov.govuk.notifications.ui

import android.Manifest
import android.os.Build
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalPermissionsApi::class)
@RunWith(AndroidJUnit4::class)
class NotificationsPermissionProviderTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @get:Rule
    val runtimePermissionRule: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.POST_NOTIFICATIONS)

    @Test
    fun given_the_Android_version_is_Tiramisu_When_get_notifications_permission_status_called_Then_granted_should_be_true() {
        composeTestRule.setContent {
            val status = getNotificationsPermissionStatus(Build.VERSION_CODES.TIRAMISU)
            assertTrue(status.isGranted)
        }
    }

    @Test
    fun given_the_Android_version_is_Snow_Cone_When_get_notifications_permission_status_called_Then_granted_should_be_true() {
        composeTestRule.setContent {
            val status = getNotificationsPermissionStatus(Build.VERSION_CODES.S_V2)
            assertTrue(status.isGranted)
        }
    }

    @Test
    fun given_the_app_is_run_for_the_first_time_When_notifications_permission_should_show_rationale_called_Then_should_return_false() {
        composeTestRule.setContent {
            val shouldShowRationale = notificationsPermissionShouldShowRationale()
            assertFalse(shouldShowRationale)
        }
    }
}
