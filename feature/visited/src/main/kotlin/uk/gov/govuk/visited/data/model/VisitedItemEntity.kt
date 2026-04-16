package uk.gov.govuk.visited.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "visited_items",
    indices = [Index(value = ["title", "url"], unique = true)]
)
internal data class VisitedItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val url: String,
    val lastVisited: Long
)
