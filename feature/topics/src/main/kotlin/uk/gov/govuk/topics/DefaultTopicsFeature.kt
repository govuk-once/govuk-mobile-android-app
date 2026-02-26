package uk.gov.govuk.topics

import uk.gov.govuk.topics.data.TopicsRepo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class DefaultTopicsFeature @Inject constructor(
    private val topicsRepo: TopicsRepo
): TopicsFeature {

    override suspend fun init() {
        topicsRepo.sync()
    }

    override suspend fun clear() {
        topicsRepo.clear()
    }

    override suspend fun hasTopics(): Boolean {
        return topicsRepo.hasTopics()
    }

}