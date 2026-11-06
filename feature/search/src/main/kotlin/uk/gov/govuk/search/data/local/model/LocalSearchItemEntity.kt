package uk.gov.govuk.search.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "local_search_items")
internal data class LocalSearchItemEntity(
    @PrimaryKey val searchTerm: String,
    val timestamp: Long
)
