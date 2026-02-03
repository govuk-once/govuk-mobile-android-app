package uk.gov.govuk.data.user.remote

import retrofit2.Response
import retrofit2.http.GET
import uk.gov.govuk.data.user.model.UserApiResponse

interface UserApi {
    // Headers are added via interceptor in UserModule.kt

    @GET("app/v1/user")
    suspend fun getUserPreferences(): Response<UserApiResponse>
}
