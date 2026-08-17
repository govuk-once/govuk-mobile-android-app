package uk.gov.govuk.search.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import uk.gov.govuk.search.data.local.model.LocalSearchItemEntity

@Database(entities = [LocalSearchItemEntity::class], version = 1, exportSchema = false)
internal abstract class SearchDatabase : RoomDatabase() {
    abstract fun searchDao(): SearchDao
}
