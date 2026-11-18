package uk.gov.govuk.chat.data.remote.model

import com.google.gson.annotations.SerializedName

data class ConversationQuestionRequest(
    @SerializedName("user_question") val userQuestion: String,
    @SerializedName("suggested_prompt") val suggestedPrompt: Boolean? = null
)
