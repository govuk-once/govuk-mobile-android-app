package uk.gov.govuk.config.data.remote.model

import com.google.gson.annotations.SerializedName

data class Link(
    @SerializedName("title") val title: String,
    @SerializedName("url") val url: String
)
