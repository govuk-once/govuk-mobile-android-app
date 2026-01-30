package uk.gov.govuk.data.flex.remote

import retrofit2.Response
import retrofit2.http.GET
import uk.gov.govuk.data.flex.model.FlexResponse

interface FlexApi {
    // Headers are added via interceptor in FlexModule.kt

    @GET("1.0/app/user")
    suspend fun getFlexPreferences(): Response<FlexResponse>
}
