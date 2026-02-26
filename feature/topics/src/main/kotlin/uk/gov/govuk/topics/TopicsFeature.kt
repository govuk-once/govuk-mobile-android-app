package uk.gov.govuk.topics

interface TopicsFeature  {

    suspend fun init()

    suspend fun clear()

    suspend fun hasTopics(): Boolean

}