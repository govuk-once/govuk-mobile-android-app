package uk.gov.govuk.chat.data.remote.model

import com.google.gson.annotations.SerializedName

data class Conversation(
    @SerializedName("id") val id: String,
    @SerializedName("answered_questions") val answeredQuestions: List<AnsweredQuestion>,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("pending_question") val pendingQuestion: PendingQuestion?
)
