package uk.gov.govuk.data.notificationcentre.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import uk.gov.govuk.data.notificationcentre.model.Notification
import uk.gov.govuk.data.notificationcentre.model.UpdateNotificationRequestBody


interface NotificationCentreApi {
    companion object {
        private const val NOTIFICATIONS_PATH = "/notifications"
    }
    @GET(NOTIFICATIONS_PATH)
    suspend fun getNotifications(): Response<List<Notification>>

    @GET("$NOTIFICATIONS_PATH/{notificationId}")
    suspend fun getSingleNotification(@Path("notificationId") notificationId: String): Response<Notification?>

    @PATCH("$NOTIFICATIONS_PATH/{notificationId}/status")
    suspend fun updateNotification(@Path("notificationId") notificationId: String, @Body body: UpdateNotificationRequestBody): Response<Unit>
}