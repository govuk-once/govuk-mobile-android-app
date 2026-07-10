package uk.gov.govuk.dvla.linking.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import uk.gov.govuk.dvla.linking.remote.model.VerificationRequest
import uk.gov.govuk.dvla.linking.remote.model.VerificationResponse

internal interface LinkingApi {
    @POST("verification")
    suspend fun getVerification(@Body requestBody: VerificationRequest): Response<VerificationResponse>
}
