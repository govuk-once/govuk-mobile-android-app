package uk.gov.govuk.dvla.linking.remote.model

import com.google.gson.annotations.SerializedName

internal data class VerificationRequest(
    @SerializedName("token") val token: String
)
