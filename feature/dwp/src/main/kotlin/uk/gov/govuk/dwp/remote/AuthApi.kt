package uk.gov.govuk.dwp.remote

import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.POST

/** Provisional DVLA-shaped contract; confirm the path with DWP before wiring a real service. */
internal fun interface AuthApi {
    @POST("app/udp/v1/identity/dwp")
    suspend fun linkDwpIdentity(
        @Header("x-linking-token") token: String
    ): Response<Unit>
}
