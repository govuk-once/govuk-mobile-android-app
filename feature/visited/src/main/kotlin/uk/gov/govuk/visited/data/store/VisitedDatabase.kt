package uk.gov.govuk.visited.data.store

import androidx.room.Database
import androidx.room.RoomDatabase
import uk.gov.govuk.visited.data.model.VisitedItemEntity

@Database(entities = [VisitedItemEntity::class], version = 1, exportSchema = false)
internal abstract class VisitedDatabase : RoomDatabase() {
    abstract fun visitedDao(): VisitedDao
}
