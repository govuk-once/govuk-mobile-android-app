package uk.gov.govuk.topics.navigation

import javax.inject.Inject
import javax.inject.Singleton
import uk.gov.govuk.topics.TopicsFeature

@Singleton
class TopicsDeepLinksProvider @Inject constructor(private val topicsFeature: TopicsFeature) {
    val deepLinks: Map<String, List<String>> by lazy {
        buildMap {
            put("/topics/edit", listOf(TOPICS_EDIT_ROUTE))
            topicsFeature.topicsReferences?.forEach { topicReference ->
                put(
                    "/topics/$topicReference",
                    listOf("$TOPIC_ROUTE/$topicReference?isSubtopic=false")
                )
            }
        }
    }
}
