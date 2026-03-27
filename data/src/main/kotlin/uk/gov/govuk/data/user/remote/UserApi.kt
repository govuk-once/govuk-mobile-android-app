package uk.gov.govuk.data.user.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import uk.gov.govuk.data.user.model.User
import uk.gov.govuk.data.user.model.UpdateNotificationsRequest

interface UserApi {
    private companion object {
        const val USERS_PATH = "app/udp/v1/users"
        const val NOTIFICATIONS_PATH = "$USERS_PATH/notifications"
    }
    // Headers are added via interceptor in UserModule.kt

    @GET(USERS_PATH)
    suspend fun getUserInfo(): Response<User>

    @PATCH(NOTIFICATIONS_PATH)
    suspend fun updateNotifications(
        @Body requestBody: UpdateNotificationsRequest
    ): Response<Unit>
}
