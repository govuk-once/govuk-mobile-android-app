package uk.gov.govuk.notifications.data

import uk.gov.govuk.notifications.NotificationsProvider
import uk.gov.govuk.notifications.data.local.NotificationsDataStore
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationsRepo @Inject constructor(
    private val notificationsDataStore: NotificationsDataStore,
    private val notificationsProvider: NotificationsProvider
) {
    suspend fun isNotificationsOnboardingCompleted() =
        notificationsDataStore.isNotificationsOnboardingCompleted()

    suspend fun notificationsOnboardingCompleted() =
        notificationsDataStore.notificationsOnboardingCompleted()

    internal suspend fun isFirstPermissionRequestCompleted() =
        notificationsDataStore.isFirstPermissionRequestCompleted()

    internal suspend fun firstPermissionRequestCompleted() =
        notificationsDataStore.firstPermissionRequestCompleted()

    fun permissionGranted() = notificationsProvider.permissionGranted()

    fun consentGiven() = notificationsProvider.consentGiven()

    fun removeConsent() {
        notificationsProvider.removeConsent()
    }
}
