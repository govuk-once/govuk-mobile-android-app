package uk.gov.govuk.dwp.data.remote.model

import com.google.gson.annotations.SerializedName

data class DeductionBreakdown(
    @SerializedName("startDate") val startDate: String,
    @SerializedName("endDate") val endDate: String,
    @SerializedName("amount") val amount: String,
    @SerializedName("name") val name: String,
    @SerializedName("deductionAmount") val deductionAmount: String
)
