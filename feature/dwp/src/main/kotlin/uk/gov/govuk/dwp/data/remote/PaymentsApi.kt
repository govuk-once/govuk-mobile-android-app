package uk.gov.govuk.dwp.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import uk.gov.govuk.dwp.data.remote.model.AwardDetailsResponse
import uk.gov.govuk.dwp.data.remote.model.PaymentsQueryResponse

interface PaymentsApi {
//    TODO: Add missing endpoints

    @GET("payment-aggregator/{guid}/award-details")
    suspend fun getAwardDetails(
        @Path("guid") guid: String
    ): Response<AwardDetailsResponse>

    @GET("payment-aggregator/{guid}/payments-query")
    suspend fun getPaymentsQuery(
        @Path("guid") guid: String
    ): Response<PaymentsQueryResponse>
}
