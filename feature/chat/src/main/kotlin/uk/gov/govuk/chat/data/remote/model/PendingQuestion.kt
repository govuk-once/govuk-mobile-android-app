package uk.gov.govuk.chat.data.remote.model

import com.google.gson.annotations.SerializedName

data class PendingQuestion(
    @SerializedName("id") val id: String = "",
    @SerializedName("answer_url") val answerUrl: String = "",
    @SerializedName("conversation_id") val conversationId: String = "",
    @SerializedName("created_at") val createdAt: String = "",
    @SerializedName("message") val message: String
)
