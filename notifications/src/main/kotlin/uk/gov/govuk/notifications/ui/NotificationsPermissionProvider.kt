package uk.gov.govuk.notifications.ui

import android.Manifest
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationManagerCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun notificationsPermissionShouldShowRationale(): Boolean =
    getNotificationsPermissionStatus().shouldShowRationale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun getNotificationsPermissionStatus(androidVersion: Int = Build.VERSION.SDK_INT): PermissionStatus {
    if (androidVersion >= Build.VERSION_CODES.TIRAMISU) {
        return rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS).status
    }
    val context = LocalContext.current
    return if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
        PermissionStatus.Granted
    } else {
        PermissionStatus.Denied(shouldShowRationale = false)
    }
}
