package uk.gov.govuk.visited.data.store

import kotlinx.coroutines.flow.Flow
import uk.gov.govuk.visited.data.model.VisitedItemEntity
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class VisitedLocalDataSource @Inject constructor(
    private val dao: VisitedDao
) {

    val visitedItems: Flow<List<VisitedItemEntity>> get() = dao.getVisitedItems()

    suspend fun insertOrUpdate(title: String, url: String, lastVisited: LocalDateTime = LocalDateTime.now()) {
        val timestamp = lastVisited.toEpochSecond(ZoneOffset.UTC)
        val updated = dao.updateLastVisited(title, url, timestamp)
        if (updated == 0) {
            dao.insert(VisitedItemEntity(title = title, url = url, lastVisited = timestamp))
        }
    }

    suspend fun remove(title: String, url: String) {
        dao.delete(title, url)
    }

    suspend fun clear() {
        dao.deleteAll()
    }
}
