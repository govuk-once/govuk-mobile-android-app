package uk.gov.govuk.dwp.data.remote.model

import com.google.gson.annotations.SerializedName

data class PaymentsQueryResponse(
    @SerializedName("status") val status: String,
    @SerializedName("statusCode") val statusCode: String,
    @SerializedName("data") val data: List<PaymentsQueryData>
)
