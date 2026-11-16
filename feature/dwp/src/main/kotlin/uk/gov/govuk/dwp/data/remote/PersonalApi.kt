package uk.gov.govuk.dwp.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import uk.gov.govuk.dwp.data.remote.model.CoreBioResponse

interface PersonalApi {
//    TODO: Add missing endpoints

    @GET("personal-aggregator/{guid}/core-bio")
    suspend fun getCoreBio(
        @Path("guid") guid: String
    ): Response<CoreBioResponse>
}
