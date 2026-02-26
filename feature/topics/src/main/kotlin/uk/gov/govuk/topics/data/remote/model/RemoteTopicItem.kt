package uk.gov.govuk.topics.data.remote.model

import com.google.gson.annotations.SerializedName

internal data class RemoteTopicItem(
    @SerializedName("ref") val ref: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String
)