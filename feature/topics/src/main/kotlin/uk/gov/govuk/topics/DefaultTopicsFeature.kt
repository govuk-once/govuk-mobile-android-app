package uk.gov.govuk.topics

import kotlinx.coroutines.flow.firstOrNull
import uk.gov.govuk.topics.data.TopicsRepo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class DefaultTopicsFeature @Inject constructor(
    private val topicsRepo: TopicsRepo
) : TopicsFeature {

    override var topicReferences: List<String>? = null

    override suspend fun init() {
        topicsRepo.sync()

        topicReferences = getTopicReferences()
    }

    override suspend fun clear() {
        topicsRepo.clear()
    }

    override suspend fun hasTopics(): Boolean {
        return topicsRepo.hasTopics()
    }

    private suspend fun getTopicReferences() = topicsRepo.topics.firstOrNull()?.map { it.ref }
}
