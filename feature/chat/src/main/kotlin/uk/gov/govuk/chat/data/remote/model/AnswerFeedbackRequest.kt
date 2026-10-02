package uk.gov.govuk.chat.data.remote.model

import com.google.gson.annotations.SerializedName

data class AnswerFeedbackRequest(
    @SerializedName("reaction") val reaction: String
)
