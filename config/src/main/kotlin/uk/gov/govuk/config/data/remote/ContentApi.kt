package uk.gov.govuk.config.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

fun interface ContentApi {
    // This requires a placeholder URL in providesContentApi
    @GET
    suspend fun getContent(@Url url: String?): Response<String>
}
