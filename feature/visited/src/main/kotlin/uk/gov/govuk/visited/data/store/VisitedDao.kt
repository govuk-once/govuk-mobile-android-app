package uk.gov.govuk.visited.data.store

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import uk.gov.govuk.visited.data.model.VisitedItemEntity

@Dao
internal interface VisitedDao {

    @Query("SELECT * FROM visited_items ORDER BY lastVisited DESC")
    fun getVisitedItems(): Flow<List<VisitedItemEntity>>

    @Upsert
    suspend fun upsert(item: VisitedItemEntity)

    @Query("DELETE FROM visited_items WHERE title = :title AND url = :url")
    suspend fun delete(title: String, url: String)

    @Query("DELETE FROM visited_items")
    suspend fun deleteAll()
}
