package uk.gov.govuk.data.remote

import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AuthApi {

    @FormUrlEncoded
    @POST("revoke")
    suspend fun revoke(
        @Field("token") refreshToken: String,
        @Field("client_id") clientId: String
    ) : Response<Unit>

}