package uk.gov.govuk.data.user.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import uk.gov.govuk.data.user.model.UpdateUser
import uk.gov.govuk.data.user.model.User
import uk.gov.govuk.data.user.model.UpdateTermsAndConditionsRequest
import uk.gov.govuk.data.user.model.UpdateNotificationsRequest

interface UserApi {
    // Headers are added via interceptor in UserModule.kt

    @GET("app/v1/user")
    suspend fun getUserInfo(): Response<User>

    @PATCH("app/v1/notifications")
    suspend fun updateNotifications(
        @Body requestBody: UpdateNotificationsRequest
    ): Response<UpdateUser>

    @PATCH("app/v1/termsAndConditions")
    suspend fun updateTermsAndConditions(
        @Body requestBody: UpdateTermsAndConditionsRequest
    ): Response<UpdateUser>
}
