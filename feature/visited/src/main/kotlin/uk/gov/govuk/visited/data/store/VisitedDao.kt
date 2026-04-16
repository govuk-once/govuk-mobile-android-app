package uk.gov.govuk.visited.data.store

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import uk.gov.govuk.visited.data.model.VisitedItemEntity

@Dao
internal interface VisitedDao {

    @Query("SELECT * FROM visited_items ORDER BY lastVisited DESC")
    fun getVisitedItems(): Flow<List<VisitedItemEntity>>

    @Query("SELECT * FROM visited_items WHERE title = :title AND url = :url LIMIT 1")
    suspend fun findByTitleAndUrl(title: String, url: String): VisitedItemEntity?

    @Insert
    suspend fun insert(item: VisitedItemEntity)

    @Query("UPDATE visited_items SET lastVisited = :lastVisited WHERE title = :title AND url = :url")
    suspend fun updateLastVisited(title: String, url: String, lastVisited: Long)

    @Query("DELETE FROM visited_items WHERE title = :title AND url = :url")
    suspend fun delete(title: String, url: String)

    @Query("DELETE FROM visited_items")
    suspend fun deleteAll()
}
