package uk.gov.govuk.dvla.linking.data

import uk.gov.govuk.data.auth.AuthRepo
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.data.remote.safeAuthApiCall
import uk.gov.govuk.dvla.linking.remote.LinkingApi
import uk.gov.govuk.dvla.linking.remote.model.VerificationRequest
import uk.gov.govuk.dvla.linking.remote.model.VerificationResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class LinkingRepo @Inject constructor(
    private val api: LinkingApi,
    private val authRepo: AuthRepo
) {
    suspend fun getVerification(): Result<VerificationResponse> {
        val result = safeAuthApiCall(
            { api.getVerification(VerificationRequest(authRepo.getAccessToken())) },
            authRepo
        )
        return result
    }
}
