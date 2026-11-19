package uk.gov.govuk.dwp.data.remote.model

import com.google.gson.annotations.SerializedName

data class AwardBreakdown(
    @SerializedName("startDate") val startDate: String,
    @SerializedName("endDate") val endDate: String,
    @SerializedName("PIL") val pil: String,
    @SerializedName("rate") val rate: String,
    @SerializedName("category") val category: String,
    @SerializedName("amount") val amount: String,
    @SerializedName("name") val name: String
)
