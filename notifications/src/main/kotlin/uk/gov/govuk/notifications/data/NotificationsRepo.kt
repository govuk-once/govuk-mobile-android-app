package uk.gov.govuk.notifications.data

import uk.gov.govuk.data.user.UserRepo
import uk.gov.govuk.data.user.UserApiResult
import uk.gov.govuk.data.user.model.UserApiResponse
import uk.gov.govuk.notifications.NotificationsProvider
import uk.gov.govuk.notifications.data.local.NotificationsDataStore
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationsRepo @Inject constructor(
    private val notificationsDataStore: NotificationsDataStore,
    private val notificationsProvider: NotificationsProvider,
    private val userRepo: UserRepo
) {
    suspend fun login(): UserApiResult<UserApiResponse> {
        val result = userRepo.getUserPreferences()
        when (result) {
            is UserApiResult.Success -> notificationsProvider.login(result.value.notificationId)
            else -> { /* Do nothing */ }
        }
        return result
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
