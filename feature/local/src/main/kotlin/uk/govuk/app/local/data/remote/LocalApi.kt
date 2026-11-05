package uk.govuk.app.local.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import uk.govuk.app.local.data.remote.model.LocalAuthorityResponse

interface LocalApi {
    @GET("api/local-authority")
    suspend fun fromPostcode(
        @Query("postcode") postcode: String
    ) : Response<LocalAuthorityResponse>

    @GET("api/local-authority/{slug}")
    suspend fun fromSlug(
        @Path("slug") slug: String
    ) : Response<LocalAuthorityResponse>
}
