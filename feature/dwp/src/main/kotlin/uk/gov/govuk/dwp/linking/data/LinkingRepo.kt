package uk.gov.govuk.dwp.linking.data

import javax.inject.Inject
import javax.inject.Singleton
import uk.gov.govuk.data.auth.AuthRepo
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.data.remote.safeAuthApiCall
import uk.gov.govuk.dwp.linking.remote.LinkingApi
import uk.gov.govuk.dwp.linking.remote.model.VerificationRequest
import uk.gov.govuk.dwp.linking.remote.model.VerificationResponse

@Singleton
internal class LinkingRepo @Inject constructor(
    private val api: LinkingApi,
    private val authRepo: AuthRepo
) {
    suspend fun getVerification(): Result<VerificationResponse> = safeAuthApiCall(
        { api.getVerification(VerificationRequest(authRepo.getAccessToken())) },
        authRepo
    )
}
