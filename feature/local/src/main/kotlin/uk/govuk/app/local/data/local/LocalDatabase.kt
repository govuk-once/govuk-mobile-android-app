package uk.govuk.app.local.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import uk.govuk.app.local.data.local.model.LocalAuthorityEntity

@Database(entities = [LocalAuthorityEntity::class], version = 1, exportSchema = false)
internal abstract class LocalDatabase : RoomDatabase() {
    abstract fun localDao(): LocalDao
}
