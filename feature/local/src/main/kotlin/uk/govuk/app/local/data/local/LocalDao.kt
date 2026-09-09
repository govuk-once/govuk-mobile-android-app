package uk.govuk.app.local.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import uk.govuk.app.local.data.local.model.LocalAuthorityEntity

@Dao
internal interface LocalDao {

    @Query("SELECT * FROM local_authority LIMIT 1")
    fun getLocalAuthority(): Flow<LocalAuthorityEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(entity: LocalAuthorityEntity)

    @Query("DELETE FROM local_authority")
    suspend fun deleteAll()
}
