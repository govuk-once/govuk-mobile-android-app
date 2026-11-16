package uk.gov.govuk.dwp.data.remote.model

import com.google.gson.annotations.SerializedName

data class BioData(
    @SerializedName("dob") val dob: String,
    @SerializedName("firstName") val firstName: String,
    @SerializedName("lastName") val lastName: String,
    @SerializedName("middleName") val middleName: String? = null
)
