package uk.gov.govuk.dvla.linking.remote.model

import com.google.gson.annotations.SerializedName

internal data class VerificationResponse(
    @SerializedName("verificationHash") val verificationHash: String
)
