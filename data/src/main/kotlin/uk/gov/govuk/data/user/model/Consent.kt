package uk.gov.govuk.data.user.model

import com.google.gson.annotations.SerializedName

data class Consent(
    @SerializedName("consentStatus") val consentStatus: ConsentStatus,
    @SerializedName("updatedAt") val updatedAt: String
)
