package uk.gov.govuk.visited.data.store

import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import uk.gov.govuk.visited.data.model.VisitedItem
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class VisitedLocalDataSource @Inject constructor(
    private val realmProvider: VisitedRealmProvider
) {

    val visitedItems: Flow<List<VisitedItem>> = flow {
        emitAll(
            realmProvider.open().query<VisitedItem>().asFlow().map {
                it.list.sortedByDescending { it.lastVisited }
            }
        )
    }

    suspend fun insertOrUpdate(title: String, url: String, lastVisited: LocalDateTime = LocalDateTime.now()) {
        realmProvider.open().write {
            val visitedItem = query<VisitedItem>("title = $0 AND url = $1", title, url).first().find()

            visitedItem?.apply {
                this.lastVisited = lastVisited.toEpochSecond(ZoneOffset.UTC)
            } ?: copyToRealm(
                VisitedItem().apply {
                    this.title = title
                    this.url = url
                    this.lastVisited = lastVisited.toEpochSecond(ZoneOffset.UTC)
                }
            )
        }
    }

    suspend fun remove(title: String, url: String) {
        realmProvider.open().write {
            val visitedItem = query<VisitedItem>("title = $0 AND url = $1", title, url).first().find()

            visitedItem?.apply {
                delete(this)
            }
        }
    }

    suspend fun clear() {
        realmProvider.open().write {
            deleteAll()
        }
    }
}
