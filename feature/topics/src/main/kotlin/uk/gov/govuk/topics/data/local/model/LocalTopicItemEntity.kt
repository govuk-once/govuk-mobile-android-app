package uk.gov.govuk.topics.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "topic_items")
internal data class LocalTopicItemEntity(
    @PrimaryKey val ref: String,
    val title: String,
    val description: String,
    val isSelected: Boolean = false
)
