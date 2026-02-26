package uk.gov.govuk.notifications.data

import uk.gov.govuk.notifications.data.local.NotificationsDataStore
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationsRepo @Inject constructor(
    private val notificationsDataStore: NotificationsDataStore
) {
    suspend fun isNotificationsOnboardingCompleted() =
        notificationsDataStore.isNotificationsOnboardingCompleted()

    suspend fun notificationsOnboardingCompleted() =
        notificationsDataStore.notificationsOnboardingCompleted()

    internal suspend fun isFirstPermissionRequestCompleted() =
        notificationsDataStore.isFirstPermissionRequestCompleted()

    internal suspend fun firstPermissionRequestCompleted() =
        notificationsDataStore.firstPermissionRequestCompleted()
}
