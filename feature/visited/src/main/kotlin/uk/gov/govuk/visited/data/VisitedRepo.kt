package uk.gov.govuk.visited.data

import kotlinx.coroutines.flow.map
import uk.gov.govuk.visited.data.store.VisitedLocalDataSource
import uk.gov.govuk.visited.domain.model.VisitedItemUi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class VisitedRepo @Inject constructor(
    private val localDataSource: VisitedLocalDataSource,
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

    suspend fun clear() {
        localDataSource.clear()
    }
}
