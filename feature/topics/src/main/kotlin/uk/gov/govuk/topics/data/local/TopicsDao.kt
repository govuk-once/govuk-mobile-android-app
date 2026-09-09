package uk.gov.govuk.topics.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import uk.gov.govuk.topics.data.local.model.LocalTopicItemEntity

@Dao
internal interface TopicsDao {

    @Query("SELECT * FROM topic_items")
    fun getTopics(): Flow<List<LocalTopicItemEntity>>

    @Query("SELECT ref FROM topic_items")
    suspend fun getAllRefs(): List<String>

    @Insert
    suspend fun insert(item: LocalTopicItemEntity)

    @Query("UPDATE topic_items SET title = :title, description = :description WHERE ref = :ref")
    suspend fun updateTitleAndDescription(ref: String, title: String, description: String)

    @Query("UPDATE topic_items SET title = :title, description = :description, isSelected = 0 WHERE ref = :ref")
    suspend fun updateTitleDescriptionAndClearSelection(ref: String, title: String, description: String)

    @Query("UPDATE topic_items SET isSelected = :isSelected WHERE ref = :ref")
    suspend fun toggleSelection(ref: String, isSelected: Boolean)

    @Query("UPDATE topic_items SET isSelected = 1 WHERE ref IN (:refs)")
    suspend fun selectAll(refs: List<String>)

    @Query("UPDATE topic_items SET isSelected = 0")
    suspend fun clearAllSelections()

    @Query("DELETE FROM topic_items WHERE ref = :ref")
    suspend fun delete(ref: String)

    @Query("SELECT COUNT(*) FROM topic_items")
    suspend fun count(): Int
}
