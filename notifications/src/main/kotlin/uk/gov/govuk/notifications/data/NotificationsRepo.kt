package uk.gov.govuk.notifications.data

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.config.data.flags.FlagRepo
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.data.user.UserRepo
import uk.gov.govuk.data.user.model.ConsentStatus
import uk.gov.govuk.notifications.NotificationsProvider
import uk.gov.govuk.notifications.data.local.NotificationsDataStore
import uk.gov.govuk.notifications.di.NotificationsScope

@Singleton
class NotificationsRepo @Inject constructor(
    private val notificationsDataStore: NotificationsDataStore,
    private val notificationsProvider: NotificationsProvider,
    private val userRepo: UserRepo,
    private val analyticsClient: AnalyticsClient,
    private val flagRepo: FlagRepo,
    @param:NotificationsScope private val scope: CoroutineScope
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
        // login() normally runs once the user state loads at sign-in or app start. If that load
        // failed this session, login() never ran, so this device isn't linked to the user until
        // the next app start, and any push sent to them before then fails. Load it now instead.
        //
        // It runs in the background so callers aren't held up by the network call, in
        // particular the OS permission prompt that usually follows consent. The flex flag gates
        // the user API, as it does at sign-in.
        if (userRepo.notifications == null && flagRepo.isFlexEnabled()) {
            scope.launch {
                if (userRepo.initUser() is Result.Success) {
                    login()
                }
            }
        }
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
