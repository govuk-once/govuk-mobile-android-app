package uk.gov.govuk.data.user.model

import com.google.gson.annotations.SerializedName

data class UpdateTermsAndConditionsRequest(
    @SerializedName("termsAndConditionsAccepted") val consentStatus: ConsentStatus
)
