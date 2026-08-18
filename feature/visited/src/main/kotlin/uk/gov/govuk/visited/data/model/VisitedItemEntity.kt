package uk.gov.govuk.visited.data.model

import androidx.room.Entity

@Entity(tableName = "visited_items", primaryKeys = ["title", "url"])
internal data class VisitedItemEntity(
    val title: String,
    val url: String,
    val lastVisited: Long
)
