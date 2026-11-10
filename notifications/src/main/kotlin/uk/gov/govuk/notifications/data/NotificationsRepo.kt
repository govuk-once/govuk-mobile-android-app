package uk.gov.govuk.notifications.data

import javax.inject.Inject
import javax.inject.Singleton
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.data.user.UserRepo
import uk.gov.govuk.data.user.model.ConsentStatus
import uk.gov.govuk.notifications.NotificationsProvider
import uk.gov.govuk.notifications.data.local.NotificationsDataStore

@Singleton
class NotificationsRepo @Inject constructor(
    private val notificationsDataStore: NotificationsDataStore,
    private val notificationsProvider: NotificationsProvider,
    private val userRepo: UserRepo
) {
    suspend fun login() {
        userRepo.notifications?.let { notifications ->
            notificationsProvider.login(notifications.pushId)
            // Todo - will be re-added for phase 2 or 3 of Hello UDP
            /*
            if (isNotificationsOnboardingCompleted()) {
                sendExistingConsentWhenPreferenceUnknown(ConsentStatus.ACCEPTED)
            }
             */
        }
    }

    private suspend fun sendExistingConsentWhenPreferenceUnknown(
        consentPreference: ConsentStatus
    ) {
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

    suspend fun requestPermission(): Boolean = notificationsProvider.requestPermission()

    fun permissionGranted() = notificationsProvider.permissionGranted()

    fun consentGiven() = notificationsProvider.consentGiven()

    fun giveConsent() {
        notificationsProvider.giveConsent()
    }

    fun removeConsent() {
        notificationsProvider.removeConsent()
    }

    suspend fun sendConsent(): Result<Unit> = userRepo.updateNotifications(ConsentStatus.ACCEPTED)

    suspend fun sendRemoveConsent(): Result<Unit> = userRepo.updateNotifications(ConsentStatus.DENIED)

    suspend fun isNotificationsOnboardingCompleted() = notificationsDataStore.isNotificationsOnboardingCompleted()

    suspend fun notificationsOnboardingCompleted() = notificationsDataStore.notificationsOnboardingCompleted()

    suspend fun isFirstPermissionRequestCompleted() = notificationsDataStore.isFirstPermissionRequestCompleted()

    suspend fun firstPermissionRequestCompleted() = notificationsDataStore.firstPermissionRequestCompleted()
}
