package uk.gov.govuk.topics.data

import kotlinx.coroutines.flow.map
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.data.model.Result.Success
import uk.gov.govuk.data.remote.safeApiCall
import uk.gov.govuk.topics.data.local.TopicsLocalDataSource
import uk.gov.govuk.topics.data.remote.TopicsApi
import uk.gov.govuk.topics.data.remote.model.RemoteTopic
import uk.gov.govuk.topics.data.remote.model.RemoteTopic.RemoteTopicContent
import uk.gov.govuk.topics.domain.model.TopicItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class TopicsRepo @Inject constructor(
    private val topicsApi: TopicsApi,
    private val localDataSource: TopicsLocalDataSource,
) {

    private var _stepBySteps: List<RemoteTopicContent> = emptyList()
    private var _popularPages: List<RemoteTopicContent> = emptyList()

    val stepBySteps
        get() = _stepBySteps

    val popularPages
        get() = _popularPages

    suspend fun sync() {
        val result = safeApiCall { topicsApi.getTopics() }
        if (result is Success){
            localDataSource.sync(result.value)
        }
    }

    val topics = localDataSource.topics.map { localTopics ->
        localTopics.map { localTopic ->
            TopicItem(
                ref = localTopic.ref,
                title = localTopic.title,
                description = localTopic.description,
                isSelected = localTopic.isSelected
            )
        }
    }

    suspend fun hasTopics(): Boolean {
        return localDataSource.hasTopics()
    }

    suspend fun toggleSelection(ref: String, isSelected: Boolean) {
        localDataSource.toggleSelection(ref, isSelected)
    }

    suspend fun selectAll(refs: List<String>) {
        localDataSource.selectAll(refs)
    }

    suspend fun getTopic(ref: String): Result<RemoteTopic> {
        val result = safeApiCall { topicsApi.getTopic(ref) }
        if (result is Success) {
            _stepBySteps = result.value.content.filter { it.isStepByStep }
            _popularPages = result.value.content.filter { it.isPopular }
        }
        return result
    }

    internal suspend fun isTopicsCustomised(): Boolean {
        return localDataSource.isTopicsCustomised()
    }

    internal suspend fun topicsCustomised() {
        localDataSource.topicsCustomised()
    }

    suspend fun clear() {
        localDataSource.clear()
    }
}
