package uk.gov.govuk.dwp.data.remote.model

import com.google.gson.annotations.SerializedName

data class CoreBioResponse(
    @SerializedName("status") val status: String,
    @SerializedName("statusCode") val statusCode: Int,
    @SerializedName("data") val data: BioData
)
