package uk.gov.govuk.visited.ui.model

import java.util.UUID

data class VisitedUi (
    val id: UUID = UUID.randomUUID(),
    val title: String,
    val url: String,
    val lastVisited: String,
    var isSelected: Boolean = false
)
