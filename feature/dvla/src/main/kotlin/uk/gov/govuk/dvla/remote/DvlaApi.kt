package uk.gov.govuk.dvla.remote

import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Path

interface DvlaApi {

    @POST("app/udp/v1/identity/dvla/{id}")
    suspend fun linkDvlaIdentity(@Path("id") id: String): Response<Unit>
}
