package uk.gov.govuk.topics.data.local

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import uk.gov.govuk.topics.data.local.model.LocalTopicItemEntity
import uk.gov.govuk.topics.data.remote.model.RemoteTopicItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class TopicsLocalDataSource @Inject constructor(
    private val dao: TopicsDao,
    private val topicsDataStore: TopicsDataStore
) {

    val topics: Flow<List<LocalTopicItemEntity>> get() = dao.getTopics()

    suspend fun sync(
        remoteTopics: List<RemoteTopicItem>,
        dispatcher: CoroutineDispatcher = Dispatchers.IO
    ) {
        withContext(dispatcher) {
            val localRefs = dao.getAllRefs().toSet()
            val remoteRefs = remoteTopics.map { it.ref }.toSet()

            (localRefs - remoteRefs).forEach { dao.delete(it) }

            val isCustomised = topicsDataStore.isTopicsCustomised()

            remoteTopics.forEach { topic ->
                if (topic.ref in localRefs) {
                    if (isCustomised) {
                        dao.updateTitleAndDescription(topic.ref, topic.title, topic.description)
                    } else {
                        // Previous impl initially marked all topics as selected by default,
                        // we need to clear this for the new impl
                        // if the user has not actively customised their topics
                        dao.updateTitleDescriptionAndClearSelection(topic.ref, topic.title, topic.description)
                    }
                } else {
                    dao.insert(LocalTopicItemEntity(ref = topic.ref, title = topic.title, description = topic.description))
                }
            }
        }
    }

    suspend fun hasTopics(): Boolean = dao.count() > 0

    suspend fun toggleSelection(ref: String, isSelected: Boolean) {
        dao.toggleSelection(ref, isSelected)
    }

    suspend fun selectAll(refs: List<String>) {
        dao.selectAll(refs)
    }

    internal suspend fun isTopicsCustomised(): Boolean {
        return topicsDataStore.isTopicsCustomised()
    }

    internal suspend fun topicsCustomised() {
        topicsDataStore.topicsCustomised()
    }

    suspend fun clear() {
        dao.clearAllSelections()
        topicsDataStore.clear()
    }
}
