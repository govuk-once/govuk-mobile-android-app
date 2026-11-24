package uk.gov.govuk.dwp.linking.remote.model

import com.google.gson.annotations.SerializedName

internal data class VerificationRequest(
    @SerializedName("token") val token: String
)
