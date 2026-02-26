package uk.gov.govuk.search.data.local

import io.realm.kotlin.ext.query
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import uk.gov.govuk.search.data.local.model.LocalSearchItem
import uk.gov.govuk.search.domain.SearchConfig
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class SearchLocalDataSource @Inject constructor(
    private val realmProvider: SearchRealmProvider,
) {

    val previousSearches: Flow<List<LocalSearchItem>> = flow {
        emitAll(
            realmProvider.open()
                .query<LocalSearchItem>()
                .sort("timestamp", Sort.DESCENDING)
                .asFlow()
                .map { it.list }
        )
    }

    suspend fun insertOrUpdatePreviousSearch(searchTerm: String) {
        realmProvider.open().write {
            val localSearch = query<LocalSearchItem>("searchTerm = $0", searchTerm).first().find()
            val now = Calendar.getInstance().timeInMillis

            localSearch?.apply {
                this.timestamp = now
            } ?: run {
                copyToRealm(
                    LocalSearchItem().apply {
                        this.searchTerm = searchTerm
                        this.timestamp = now
                    }
                )

                val localSearches = query<LocalSearchItem>().sort("timestamp", Sort.DESCENDING).find()
                if (localSearches.size > SearchConfig.MAX_PREVIOUS_SEARCH_COUNT) {
                    localSearches.drop(SearchConfig.MAX_PREVIOUS_SEARCH_COUNT).forEach {
                        findLatest(it)?.apply {
                            delete(this)
                        }
                    }
                }
            }
        }
    }

    suspend fun removePreviousSearch(searchTerm: String) {
        realmProvider.open().write {
            val localSearch = query<LocalSearchItem>("searchTerm = $0", searchTerm).first().find()

            localSearch?.let {
                findLatest(it)?.apply {
                    delete(this)
                }
            }
        }
    }

    suspend fun removeAllPreviousSearches() {
        realmProvider.open().write {
            deleteAll()
        }
    }

    suspend fun clear() {
        removeAllPreviousSearches()
    }
}