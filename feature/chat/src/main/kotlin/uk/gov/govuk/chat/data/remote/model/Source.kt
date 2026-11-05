package uk.gov.govuk.chat.data.remote.model

import com.google.gson.annotations.SerializedName

data class Source(
    @SerializedName("url") val url: String,
    @SerializedName("title") val title: String
)
