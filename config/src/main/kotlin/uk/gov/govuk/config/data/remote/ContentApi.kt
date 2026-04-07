package uk.gov.govuk.config.data.remote

import retrofit2.Response
import retrofit2.http.GET

interface ContentApi {
    @GET("content/guidance/govuk-app-terms-and-conditions")
    suspend fun getContent(): Response<String>
}
