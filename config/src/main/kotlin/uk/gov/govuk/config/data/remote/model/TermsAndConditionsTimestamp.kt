package uk.gov.govuk.config.data.remote.model

import com.google.gson.annotations.SerializedName

data class TermsAndConditionsTimestamp(
    @SerializedName("public_updated_at") val publicUpdatedAt: String
)
