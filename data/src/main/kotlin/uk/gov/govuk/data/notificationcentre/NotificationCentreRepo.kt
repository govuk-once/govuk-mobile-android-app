package uk.gov.govuk.data.notificationcentre

import uk.gov.govuk.data.model.Result
import uk.gov.govuk.data.notificationcentre.model.Notification
import uk.gov.govuk.data.notificationcentre.model.UpdateNotificationRequestBody

interface NotificationCentreRepo {
    suspend fun getNotifications(): Result<List<Notification>>
    suspend fun getSingleNotification(notificationId: String): Result<Notification?>
    suspend fun updateNotification(notificationId: String, status: UpdateNotificationRequestBody.Status): Result<Unit>
}