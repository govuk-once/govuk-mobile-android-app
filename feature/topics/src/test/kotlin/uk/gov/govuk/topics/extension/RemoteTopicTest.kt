package uk.gov.govuk.topics.extension

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import uk.gov.govuk.topics.R
import uk.gov.govuk.topics.data.remote.model.RemoteTopic
import uk.gov.govuk.topics.data.remote.model.RemoteTopic.RemoteTopicContent
import uk.gov.govuk.topics.data.remote.model.RemoteTopicItem
import uk.gov.govuk.topics.ui.model.TopicUi
import uk.gov.govuk.topics.ui.model.TopicUi.TopicContent

class RemoteTopicTest {

    @Test
    fun `Given a remote topic, When mapping to topic ui, then return a topic ui`() {
        val remoteTopic = RemoteTopic(
            title = "title",
            description = "description",
            subtopics = listOf(
                RemoteTopicItem(
                    ref = "ref",
                    title = "title",
                    description = "description",
                )
            ),
            content = listOf(
                RemoteTopicContent(
                    url = "url-1",
                    title = "title-1",
                    isStepByStep = true,
                    isPopular = false
                ),
                RemoteTopicContent(
                    url = "url-2",
                    title = "title-2",
                    isStepByStep = false,
                    isPopular = true
                ),
                RemoteTopicContent(
                    url = "url-3",
                    title = "title-3",
                    isStepByStep = false,
                    isPopular = false
                )
            )
        )

        val popularPages = listOf(
            TopicContent(
                url = "url-2",
                title = "title-2"
            )
        )

        val stepBySteps = listOf(
            TopicContent(
                url = "url-1",
                title = "title-1"
            )
        )

        val services = listOf(
            TopicContent(
                url = "url-3",
                title = "title-3"
            )
        )

        val subtopics = listOf(
            TopicUi.Subtopic(
                ref = "ref",
                title = "title"
            )
        )

        val topicUi = remoteTopic.toTopicUi(1, 1, false)
        assertEquals("title", topicUi.title)
        assertEquals("description", topicUi.description)
        assertEquals(popularPages, topicUi.popularPages)
        assertEquals(stepBySteps, topicUi.stepBySteps)
        assertEquals(services, topicUi.services)
        assertEquals(subtopics, topicUi.subtopics)
    }

    @Test
    fun `Given a remote topic with step by steps that exceed the max number, When mapping to topic ui, then return a topic ui`() {
        val remoteTopic = RemoteTopic(
            title = "title",
            description = "description",
            subtopics = listOf(
                RemoteTopicItem(
                    ref = "ref",
                    title = "title",
                    description = "description",
                )
            ),
            content = listOf(
                RemoteTopicContent(
                    url = "url-1",
                    title = "title-1",
                    isStepByStep = true,
                    isPopular = false
                ),
                RemoteTopicContent(
                    url = "url-2",
                    title = "title-2",
                    isStepByStep = false,
                    isPopular = true
                ),
                RemoteTopicContent(
                    url = "url-3",
                    title = "title-3",
                    isStepByStep = true,
                    isPopular = false
                )
            )
        )

        val stepBySteps = listOf(
            TopicContent(
                url = "url-1",
                title = "title-1"
            )
        )

        val topicUi = remoteTopic.toTopicUi(1, 1, false)
        assertEquals(stepBySteps, topicUi.stepBySteps)
        assertTrue(topicUi.displayStepByStepSeeAll)
    }

    @Test
    fun `Given a remote topic with popular pages that exceed the max number, When mapping to topic ui, then return a topic ui`() {
        val remoteTopic = RemoteTopic(
            title = "title",
            description = "description",
            subtopics = listOf(
                RemoteTopicItem(
                    ref = "ref",
                    title = "title",
                    description = "description",
                )
            ),
            content = listOf(
                RemoteTopicContent(
                    url = "url-1",
                    title = "title-1",
                    isStepByStep = false,
                    isPopular = true
                ),
                RemoteTopicContent(
                    url = "url-2",
                    title = "title-2",
                    isStepByStep = false,
                    isPopular = true
                ),
                RemoteTopicContent(
                    url = "url-3",
                    title = "title-3",
                    isStepByStep = false,
                    isPopular = true
                )
            )
        )

        val popularPages = listOf(
            TopicContent(
                url = "url-1",
                title = "title-1"
            )
        )

        val topicUi = remoteTopic.toTopicUi(1, 1, false)
        assertEquals(popularPages, topicUi.popularPages)
        assertTrue(topicUi.displayPopularPagesSeeAll)
    }

    @Test
    fun `Given a remote topic, When mapping to topic ui, then return with subtopic title and icon of browse`() {
        val remoteTopic = RemoteTopic(
            title = "title",
            description = "description",
            subtopics = emptyList(),
            content = emptyList()
        )

        val topicUi = remoteTopic.toTopicUi(1, 1, false)
        assertEquals(R.string.browse_title, topicUi.subtopicsSection.title)
        assertEquals(R.drawable.ic_topic_browse, topicUi.subtopicsSection.icon)
    }

    @Test
    fun `Given a remote subtopic with empty content, When mapping to topic ui, then return with subtopic title and icon of browse`() {
        val remoteTopic = RemoteTopic(
            title = "title",
            description = "description",
            subtopics = emptyList(),
            content = emptyList()
        )

        val topicUi = remoteTopic.toTopicUi(1, 1, true)
        assertEquals(R.string.browse_title, topicUi.subtopicsSection.title)
        assertEquals(R.drawable.ic_topic_browse, topicUi.subtopicsSection.icon)
    }

    @Test
    fun `Given a remote subtopic with content, When mapping to topic ui, then return with subtopic title and icon of related`() {
        val remoteTopic = RemoteTopic(
            title = "title",
            description = "description",
            subtopics = emptyList(),
            content = listOf(
                RemoteTopicContent(
                    url = "url",
                    title = "title",
                    isStepByStep = false,
                    isPopular = false
                )
            )
        )

        val topicUi = remoteTopic.toTopicUi(1, 1, true)
        assertEquals(R.string.related_title, topicUi.subtopicsSection.title)
        assertEquals(R.drawable.ic_topic_related, topicUi.subtopicsSection.icon)
    }
}
