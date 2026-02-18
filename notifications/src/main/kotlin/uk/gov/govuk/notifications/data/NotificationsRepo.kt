package uk.gov.govuk.notifications.data

import uk.gov.govuk.data.user.UserRepo
import uk.gov.govuk.notifications.NotificationsProvider
import uk.gov.govuk.notifications.data.local.NotificationsDataStore
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.data.user.model.ConsentStatus
import uk.gov.govuk.data.user.model.UpdateUserDataResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationsRepo @Inject constructor(
    private val notificationsDataStore: NotificationsDataStore,
    private val notificationsProvider: NotificationsProvider,
    private val userRepo: UserRepo
) {
    suspend fun login() {
        notificationsProvider.login(userRepo.notificationId)

        if (isNotificationsOnboardingCompleted()) {
            sendExistingConsentWhenPreferenceUnknown(userRepo.preferences.notifications.consentStatus)
        }
    }

    private suspend fun sendExistingConsentWhenPreferenceUnknown(consentPreference: ConsentStatus) {
        if (consentPreference == ConsentStatus.UNKNOWN) {
            val existingConsentStatus = when (notificationsProvider.consentGiven()) {
                true -> ConsentStatus.ACCEPTED
                false -> ConsentStatus.DENIED
            }
            userRepo.updateNotifications(existingConsentStatus)
        }
    }

    fun logout() {
        notificationsProvider.logout()
    }

    suspend fun requestPermission() {
        notificationsProvider.requestPermission()
    }

    fun permissionGranted() = notificationsProvider.permissionGranted()

    fun consentGiven() = notificationsProvider.consentGiven()

    fun giveConsent() {
        notificationsProvider.giveConsent()
    }

    fun removeConsent() {
        notificationsProvider.removeConsent()
    }

    suspend fun sendConsent(): Result<UpdateUserDataResponse> {
        return userRepo.updateNotifications(ConsentStatus.ACCEPTED)
    }

    suspend fun sendRemoveConsent(): Result<UpdateUserDataResponse> {
        return userRepo.updateNotifications(ConsentStatus.DENIED)
    }

    suspend fun isNotificationsOnboardingCompleted() =
        notificationsDataStore.isNotificationsOnboardingCompleted()

    suspend fun notificationsOnboardingCompleted() =
        notificationsDataStore.notificationsOnboardingCompleted()

    internal suspend fun isFirstPermissionRequestCompleted() =
        notificationsDataStore.isFirstPermissionRequestCompleted()

    internal suspend fun firstPermissionRequestCompleted() =
        notificationsDataStore.firstPermissionRequestCompleted()
}
