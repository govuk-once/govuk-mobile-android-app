package uk.gov.govuk.topics.data.remote.model

import com.google.gson.annotations.SerializedName

internal data class RemoteTopic(
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String?,
    @SerializedName("subtopics") val subtopics: List<RemoteTopicItem>,
    @SerializedName("content") val content: List<RemoteTopicContent>
) {
    data class RemoteTopicContent(
        @SerializedName("url") val url: String,
        @SerializedName("title") val title: String,
        @SerializedName("isStepByStep") val isStepByStep: Boolean,
        @SerializedName("popular") val isPopular: Boolean,
    )
}