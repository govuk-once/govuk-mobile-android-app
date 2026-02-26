package uk.gov.govuk.notifications

import android.Manifest
import android.app.Activity
import android.os.Build
import androidx.core.app.ActivityCompat

fun notificationsPermissionShouldShowRationale(
    activity: Activity,
    androidVersion: Int = Build.VERSION.SDK_INT
) = androidVersion >= Build.VERSION_CODES.TIRAMISU
        && ActivityCompat.shouldShowRequestPermissionRationale(
    activity,
    Manifest.permission.POST_NOTIFICATIONS
)
