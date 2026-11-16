package uk.gov.govuk.dwp.data

import javax.inject.Inject
import javax.inject.Singleton
import uk.gov.govuk.data.auth.AuthRepo
import uk.gov.govuk.dwp.data.remote.BenefitsResult
import uk.gov.govuk.dwp.data.remote.PaymentsApi
import uk.gov.govuk.dwp.data.remote.PersonalApi
import uk.gov.govuk.dwp.data.remote.model.AwardDetailsResponse
import uk.gov.govuk.dwp.data.remote.model.CoreBioResponse
import uk.gov.govuk.dwp.data.remote.model.PaymentsQueryResponse
import uk.gov.govuk.dwp.data.remote.safeBenefitsApiCall

@Singleton
internal class BenefitsRepo
@Inject
constructor(
    private val paymentsApi: PaymentsApi,
    private val personalApi: PersonalApi,
    private val authRepo: AuthRepo
) {
    suspend fun getCoreBio(
        guid: String
    ): BenefitsResult<CoreBioResponse> = safeBenefitsApiCall(
        apiCall = { personalApi.getCoreBio(guid) },
        authRepo = authRepo
    )

    suspend fun getAwardDetails(
        guid: String
    ): BenefitsResult<AwardDetailsResponse> = safeBenefitsApiCall(
        apiCall = { paymentsApi.getAwardDetails(guid) },
        authRepo = authRepo
    )

    suspend fun getPaymentsQuery(
        guid: String
    ): BenefitsResult<PaymentsQueryResponse> = safeBenefitsApiCall(
        apiCall = { paymentsApi.getPaymentsQuery(guid) },
        authRepo = authRepo
    )
}
