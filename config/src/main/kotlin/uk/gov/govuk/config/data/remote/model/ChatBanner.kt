package uk.gov.govuk.config.data.remote.model

import com.google.gson.annotations.SerializedName

data class ChatBanner(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("body") val body: String,
    @SerializedName("link") val link: Link
)
