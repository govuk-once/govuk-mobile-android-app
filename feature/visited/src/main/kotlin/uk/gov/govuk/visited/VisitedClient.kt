package uk.gov.govuk.visited

import uk.gov.govuk.visited.data.VisitedRepo
import uk.gov.govuk.visited.data.store.VisitedLocalDataSource
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class VisitedClient @Inject constructor(
    private val visitedRepo: VisitedRepo,
    private val visitedLocalDataSource: VisitedLocalDataSource
): Visited {
    override suspend fun visitableItemClick(title: String, url: String) {
        upsertVisitedItem(title, url)
    }

    private suspend fun upsertVisitedItem(title: String, url: String) {
        visitedLocalDataSource.insertOrUpdate(title, url, LocalDateTime.now())
    }

    override suspend fun clear() {
        visitedRepo.clear()
    }
}
