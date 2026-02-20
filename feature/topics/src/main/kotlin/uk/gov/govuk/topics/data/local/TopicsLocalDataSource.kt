package uk.gov.govuk.topics.data.local

import io.realm.kotlin.ext.query
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import uk.gov.govuk.topics.data.local.model.LocalTopicItem
import uk.gov.govuk.topics.data.remote.model.RemoteTopicItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class TopicsLocalDataSource @Inject constructor(
    private val realmProvider: TopicsRealmProvider,
    private val topicsDataStore: TopicsDataStore
) {

    val topics: Flow<List<LocalTopicItem>> = flow {
        emitAll(realmProvider.open().query<LocalTopicItem>().asFlow().map { it.list })
    }

    suspend fun sync(
        remoteTopics: List<RemoteTopicItem>,
        dispatcher: CoroutineDispatcher = Dispatchers.IO
    ) {
        withContext(dispatcher) {
            val localTopics = realmProvider.open().query<LocalTopicItem>().find().toList()
            val topicsToDelete =
                localTopics.filter { !remoteTopics.map { it.ref }.contains(it.ref) }

            val isTopicsCustomised = isTopicsCustomised()

            realmProvider.open().write {
                for (topic in topicsToDelete) {
                    val liveTopic = findLatest(topic)
                    if (liveTopic != null) {
                        delete(liveTopic)
                    }
                }

                remoteTopics.forEach { topic ->
                    val localTopic = query<LocalTopicItem>("ref = $0", topic.ref).first().find()

                    localTopic?.apply {
                        this.title = topic.title
                        this.description = topic.description
                        // Previous impl initially marked all topics as selected by default,
                        // we need to clear this for the new impl
                        // if the user has not actively customised their topics
                        if (!isTopicsCustomised) {
                            this.isSelected = false
                        }
                    } ?: copyToRealm(
                        LocalTopicItem().apply {
                            this.ref = topic.ref
                            this.title = topic.title
                            this.description = topic.description
                        }
                    )
                }
            }
        }
    }

    suspend fun hasTopics(): Boolean {
        return realmProvider.open().query<LocalTopicItem>().find().toList().isNotEmpty()
    }

    suspend fun toggleSelection(ref: String, isSelected: Boolean) {
        realmProvider.open().write {
            query<LocalTopicItem>("ref = $0", ref).first().find()?.apply {
                this.isSelected = isSelected
            }
        }
    }

    suspend fun selectAll(refs: List<String>) {
        realmProvider.open().write {
            val topics = query<LocalTopicItem>("ref IN $0", refs).find()
            topics.forEach { topic ->
                findLatest(topic)?.isSelected = true
            }
        }
    }

    internal suspend fun isTopicsCustomised(): Boolean {
        return topicsDataStore.isTopicsCustomised()
    }

    internal suspend fun topicsCustomised() {
        topicsDataStore.topicsCustomised()
    }

    suspend fun clear() {
        realmProvider.open().write {
            val localTopics = this.query<LocalTopicItem>().find().toList()

            for (topic in localTopics) {
                topic.apply {
                    // next user starts with all topics unselected
                    this.isSelected = false
                }
            }
        }

        topicsDataStore.clear()
    }
}