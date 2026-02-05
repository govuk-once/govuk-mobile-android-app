package uk.gov.govuk.notifications.data

import uk.gov.govuk.data.user.UserRepo
import uk.gov.govuk.data.user.model.GetUserInfoResponse
import uk.gov.govuk.notifications.NotificationsProvider
import uk.gov.govuk.notifications.data.local.NotificationsDataStore
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.data.model.Result.Success
import uk.gov.govuk.data.user.model.UpdateUserDataResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationsRepo @Inject constructor(
    private val notificationsDataStore: NotificationsDataStore,
    private val notificationsProvider: NotificationsProvider,
    private val userRepo: UserRepo
) {
    suspend fun login(): Result<GetUserInfoResponse> {
        val result = userRepo.getUserInfo()
        when (result) {
            is Success -> notificationsProvider.login(result.value.notificationId)
            else -> { /* Do nothing */ }
        }
        return result
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
        return userRepo.updateNotifications(consented = true)
    }

    suspend fun sendRemoveConsent(): Result<UpdateUserDataResponse> {
        return userRepo.updateNotifications(consented = false)
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
