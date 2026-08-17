package uk.gov.govuk.search.data.local

import kotlinx.coroutines.flow.Flow
import uk.gov.govuk.search.data.local.model.LocalSearchItemEntity
import uk.gov.govuk.search.domain.SearchConfig
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class SearchLocalDataSource @Inject constructor(
    private val dao: SearchDao,
) {

    val previousSearches: Flow<List<LocalSearchItemEntity>> get() = dao.getPreviousSearches()

    suspend fun insertOrUpdatePreviousSearch(searchTerm: String) {
        val now = Calendar.getInstance().timeInMillis
        dao.insertOrUpdate(LocalSearchItemEntity(searchTerm, now))

        val all = dao.getAllSortedByTimestamp()
        if (all.size > SearchConfig.MAX_PREVIOUS_SEARCH_COUNT) {
            all.drop(SearchConfig.MAX_PREVIOUS_SEARCH_COUNT).forEach { dao.delete(it.searchTerm) }
        }
    }

    suspend fun removePreviousSearch(searchTerm: String) {
        dao.delete(searchTerm)
    }

    suspend fun removeAllPreviousSearches() {
        dao.deleteAll()
    }

    suspend fun clear() {
        removeAllPreviousSearches()
    }
}
