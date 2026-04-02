package uk.gov.govuk.dvla.remote

import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import uk.gov.govuk.dvla.remote.model.DvlaStatusResponse

interface DvlaApi {

    @GET("app/udp/v1/identity/dvla")
    suspend fun checkDvlaLinked(): Response<DvlaStatusResponse>

    @POST("app/udp/v1/identity/dvla/{id}")
    suspend fun linkDvlaIdentity(@Path("id") id: String): Response<Unit>

    @DELETE("app/udp/v1/identity/dvla")
    suspend fun deleteDvlaIdentity(): Response<Unit>

}
