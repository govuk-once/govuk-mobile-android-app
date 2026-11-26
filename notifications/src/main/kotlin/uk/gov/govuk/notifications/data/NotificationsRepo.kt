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

    /**
     * Compares what the OneSignal SDK saved on this device with the signed-in user, and logs a
     * non-fatal if they don't match.
     *
     * Call it on app start for a returning user, before [login]. Logging in again can repair the
     * state, which would hide what the previous session left behind; that self-healing on the
     * next cold boot is what has made these failures invisible so far. Don't call it after a
     * fresh sign-in: that starts logged out of OneSignal, so a mismatch there is expected.
     */
    fun reportInconsistentState() {
        val pushId = userRepo.notifications?.pushId ?: return
        // Only users who have consented should be linked in OneSignal.
        if (!consentGiven()) return

        val state = try {
            notificationsProvider.sdkState()
        } catch (e: Exception) {
            // OneSignal throws if it failed to initialise. A diagnostic check mustn't crash the
            // app, and a failed initialisation is worth knowing about in its own right.
            analyticsClient.logException(e)
            return
        }

        // None of these depend on the OS permission: the SDK links the external ID and creates
        // the push subscription whether or not notifications are allowed.
        val externalIdMatches = state.externalId == pushId
        val onesignalIdSet = state.onesignalId.isNotEmpty()
        val pushSubscriptionIdSet = state.pushSubscriptionId.isNotEmpty()
        if (externalIdMatches && onesignalIdSet && pushSubscriptionIdSet) return

        analyticsClient.logException(
            InconsistentOneSignalStateException(
                externalIdSet = state.externalId.isNotEmpty(),
                externalIdMatches = externalIdMatches,
                onesignalIdSet = onesignalIdSet,
                pushSubscriptionIdSet = pushSubscriptionIdSet,
                pushOptedIn = state.pushOptedIn
            )
        )
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
        // Drop the signed-out user's push ID so it can never be used to log in the next user.
        userRepo.clear()
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
