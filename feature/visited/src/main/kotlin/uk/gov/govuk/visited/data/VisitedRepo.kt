package uk.gov.govuk.visited.data

import kotlinx.coroutines.flow.map
import uk.gov.govuk.visited.data.store.VisitedLocalDataSource
import uk.gov.govuk.visited.domain.model.VisitedItemUi
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class VisitedRepo @Inject constructor(
    private val localDataSource: VisitedLocalDataSource,
    private val shortcutPublisher: VisitedShortcutPublisher
) {
    val visitedItems = localDataSource.visitedItems.map { visitedItems ->
        visitedItems.map { visitedItem ->
            VisitedItemUi(
                title = visitedItem.title,
                url = visitedItem.url,
                lastVisited = visitedItem.lastVisited
            )
        }
    }

    suspend fun insertOrUpdate(title: String, url: String, lastVisited: LocalDateTime = LocalDateTime.now()) {
        localDataSource.insertOrUpdate(title, url, lastVisited)
        shortcutPublisher.pushShortcut(title, url)
    }

    suspend fun remove(title: String, url: String) {
        localDataSource.remove(title, url)
        shortcutPublisher.removeShortcut(url)
    }

    suspend fun clear() {
        localDataSource.clear()
        shortcutPublisher.clearAll()
    }
}
