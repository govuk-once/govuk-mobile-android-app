package uk.gov.govuk.chat.data.remote.model

import com.google.gson.annotations.SerializedName

data class Feedback(
    @SerializedName("reaction") val reaction: String,
    @SerializedName("created_at") val createdAt: String
)
