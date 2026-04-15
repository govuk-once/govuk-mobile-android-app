package uk.gov.govuk.config.data.remote.model

import com.google.gson.annotations.SerializedName

data class TermsAndConditions(
    @SerializedName("url") val url: String,
    @SerializedName("contentItemApiUrl") val contentItemApiUrl: String,
    @SerializedName("lastUpdated") val lastUpdated: String? = null
)
