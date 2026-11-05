package uk.gov.govuk.data.identity.remote

import retrofit2.Response
import retrofit2.http.GET


interface IdentityApi {

    @GET("/app/udp/v1/identity")
    suspend fun getLinkedServices(): Response<IdentityResponse>
}
