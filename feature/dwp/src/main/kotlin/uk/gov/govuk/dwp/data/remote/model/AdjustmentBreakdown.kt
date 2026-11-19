package uk.gov.govuk.dwp.data.remote.model

import com.google.gson.annotations.SerializedName

data class AdjustmentBreakdown(
    @SerializedName("frequency") val frequency: String,
    @SerializedName("amount") val amount: Double,
    @SerializedName("category") val category: String,
    @SerializedName("endDate") val endDate: String,
    @SerializedName("type") val type: String,
    @SerializedName("startDate") val startDate: String
)
