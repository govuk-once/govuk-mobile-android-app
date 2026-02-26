package uk.gov.govuk.chat.ui.model

import java.util.UUID

data class ChatEntry(
    val id: String = UUID.randomUUID().toString(),
    val question: String,
    var answer: String,
    var sources: List<String>?,
    val shouldAnimate: Boolean = true
)
