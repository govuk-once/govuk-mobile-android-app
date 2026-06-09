package uk.gov.govuk.data.identity.remote

import com.google.gson.annotations.SerializedName

data class IdentityResponse(
    @SerializedName("services") val services: List<String>
)