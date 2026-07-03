package uk.gov.govuk.notificationcentre

import uk.gov.govuk.data.model.Result
import uk.gov.govuk.notificationcentre.data.NotificationCentreRepo
import javax.inject.Inject

internal class DefaultNotificationCentreFeature @Inject constructor(
    private val notificationCentreRepo: NotificationCentreRepo
): NotificationCentreFeature {

    override suspend fun getUnreadCount(): Int? {
        return when (val result = notificationCentreRepo.getNotifications()) {
            is Result.Success -> result.value.count { it.isUnread }
            else -> null
        }
    }

}
