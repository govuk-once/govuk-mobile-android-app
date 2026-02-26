package uk.gov.govuk.config.data.remote

import retrofit2.Response
import retrofit2.http.GET

interface ConfigApi {
    @GET("appinfo/android")
    suspend fun getConfig(): Response<String>
}
