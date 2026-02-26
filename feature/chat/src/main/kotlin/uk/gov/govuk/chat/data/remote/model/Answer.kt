package uk.gov.govuk.chat.data.remote.model

import com.google.gson.annotations.SerializedName

data class Answer(
    @SerializedName("id") val id: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("message") val message: String,
    @SerializedName("sources") val sources: List<Source>?
)
