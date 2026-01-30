package uk.gov.govuk.notifications.data

import uk.gov.govuk.data.flex.FlexRepo
import uk.gov.govuk.data.flex.FlexResult
import uk.gov.govuk.data.flex.model.FlexResponse
import uk.gov.govuk.notifications.NotificationsProvider
import uk.gov.govuk.notifications.data.local.NotificationsDataStore
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationsRepo @Inject constructor(
    private val notificationsDataStore: NotificationsDataStore,
    private val notificationsProvider: NotificationsProvider,
    private val flexRepo: FlexRepo
) {
    suspend fun login(): FlexResult<FlexResponse> {
        val result = flexRepo.getFlexPreferences()
        when (result) {
            is FlexResult.Success -> notificationsProvider.login(result.value.userId)
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
