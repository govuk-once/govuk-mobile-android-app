package uk.gov.govuk.dwp.data.remote.model

import com.google.gson.annotations.SerializedName

data class Award(
    @SerializedName("status") val status: String,
    @SerializedName("statusCode") val statusCode: String,
    @SerializedName("data") val data: AwardData
)
