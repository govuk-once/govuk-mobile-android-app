package uk.gov.govuk.notifications

import android.os.Build
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.shouldShowRationale
import uk.gov.govuk.notifications.data.NotificationsRepo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationsPermissionResolver @Inject constructor(
    private val repo: NotificationsRepo
) {
    enum class PermissionPath {
        OS_PROMPT,
        SETTINGS,
        GRANTED
    }

    @OptIn(ExperimentalPermissionsApi::class)
    suspend fun resolve(
        status: PermissionStatus,
        sdk: Int = Build.VERSION.SDK_INT
    ): PermissionPath {
        val isDefault = sdk >= Build.VERSION_CODES.TIRAMISU &&
            !status.isGranted &&
            (!repo.isFirstPermissionRequestCompleted() || status.shouldShowRationale)

        return if (isDefault) {
            PermissionPath.OS_PROMPT
        } else {
            PermissionPath.SETTINGS
        }
    }

    suspend fun requestOsPermission(): Boolean {
        repo.firstPermissionRequestCompleted()
        repo.giveConsent()
        return repo.requestPermission()
    }
}
