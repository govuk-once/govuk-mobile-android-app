package uk.gov.govuk.notificationcentre.data

import uk.gov.govuk.data.model.Result
import uk.gov.govuk.notificationcentre.data.model.Notification
import uk.gov.govuk.notificationcentre.data.model.UpdateNotificationRequestBody
import java.time.Instant

interface DateProvider {
    val date: Instant
}

internal interface NotificationCentreRepo {
    suspend fun getNotifications(): Result<List<Notification>>
    suspend fun getSingleNotification(notificationId: String): Result<Notification?>
    suspend fun updateNotification(notificationId: String, status: UpdateNotificationRequestBody.Status): Result<Unit>
    suspend fun deleteNotification(notificationId: String): Result<Unit>
}