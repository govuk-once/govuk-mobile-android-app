package uk.gov.govuk.search.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import uk.gov.govuk.search.data.local.model.LocalSearchItemEntity

@Dao
internal interface SearchDao {

    @Query("SELECT * FROM local_search_items ORDER BY timestamp DESC")
    fun getPreviousSearches(): Flow<List<LocalSearchItemEntity>>

    @Query("SELECT * FROM local_search_items ORDER BY timestamp DESC")
    suspend fun getAllSortedByTimestamp(): List<LocalSearchItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(item: LocalSearchItemEntity)

    @Query("DELETE FROM local_search_items WHERE searchTerm = :searchTerm")
    suspend fun delete(searchTerm: String)

    @Query("DELETE FROM local_search_items")
    suspend fun deleteAll()
}
