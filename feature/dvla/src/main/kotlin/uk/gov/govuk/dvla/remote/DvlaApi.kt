package uk.gov.govuk.dvla.remote

import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Path

interface DvlaApi {

    @POST("app/v1/identity/dvla/{id}")
    suspend fun linkDvlaIdentity(@Path("id") id: String): Response<Unit>

    @DELETE("app/v1/identity/dvla")
    suspend fun deleteDvlaIdentity(): Response<Unit>

}
