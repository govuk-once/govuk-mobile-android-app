package uk.gov.govuk.data.user.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import uk.gov.govuk.data.user.model.UpdateUserDataResponse
import uk.gov.govuk.data.user.model.GetUserInfoResponse
import uk.gov.govuk.data.user.model.UpdateAnalyticsRequest
import uk.gov.govuk.data.user.model.UpdateNotificationsRequest

interface UserApi {
    // Headers are added via interceptor in UserModule.kt

    private companion object {
        const val PATH = "app/v1/user"
    }

    @GET(PATH)
    suspend fun getUserInfo(): Response<GetUserInfoResponse>

    @PATCH(PATH)
    suspend fun updateNotifications(
        @Body requestBody: UpdateNotificationsRequest
    ): Response<UpdateUserDataResponse>

    @PATCH(PATH)
    suspend fun updateAnalytics(
        @Body requestBody: UpdateAnalyticsRequest
    ): Response<UpdateUserDataResponse>
}
