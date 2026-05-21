package uk.gov.govuk.topics.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import uk.gov.govuk.topics.data.local.model.LocalTopicItemEntity

@Database(entities = [LocalTopicItemEntity::class], version = 1, exportSchema = false)
internal abstract class TopicsDatabase : RoomDatabase() {
    abstract fun topicsDao(): TopicsDao
}
