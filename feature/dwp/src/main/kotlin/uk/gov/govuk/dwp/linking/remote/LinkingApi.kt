package uk.gov.govuk.dwp.linking.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import uk.gov.govuk.dwp.linking.remote.model.VerificationRequest
import uk.gov.govuk.dwp.linking.remote.model.VerificationResponse

internal fun interface LinkingApi {
    @POST("verification")
    suspend fun getVerification(
        @Body requestBody: VerificationRequest
    ): Response<VerificationResponse>
}
