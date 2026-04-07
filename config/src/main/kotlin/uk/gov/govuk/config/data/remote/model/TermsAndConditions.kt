package uk.gov.govuk.config.data.remote.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class TermsAndConditions(
    @SerializedName("url") val url: String,
    @SerializedName("lastUpdated") val lastUpdated: String? = null,
)
