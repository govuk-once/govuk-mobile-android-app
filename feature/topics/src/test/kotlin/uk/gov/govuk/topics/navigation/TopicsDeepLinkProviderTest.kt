package uk.gov.govuk.topics.navigation

import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.topics.TopicsFeature

class TopicsDeepLinkProviderTest {

    private val topicsFeature = mockk<TopicsFeature>(relaxed = true)

    private lateinit var topicsDeepLinksProvider: TopicsDeepLinksProvider

    @Before
    fun setup() {
        topicsDeepLinksProvider = TopicsDeepLinksProvider(topicsFeature)
    }

    @Test
    fun `Given get topic references is null, When init, then deep links contains edit topics`() =
        runTest {
            every { topicsFeature.topicReferences } returns null

            val expected = mapOf("/topics/edit" to listOf("topics_edit_route"))
            assertEquals(expected, topicsDeepLinksProvider.deepLinks)
        }

    @Test
    fun `Given get topic references contains a value, When init, then deep links contains the expected values`() =
        runTest {
            every { topicsFeature.topicReferences } returns listOf("topic_name")

            val expected = mapOf(
                "/topics/edit" to listOf("topics_edit_route"),
                "/topics/topic_name" to listOf("topic_route/topic_name?isSubtopic=false")
            )
            assertEquals(expected, topicsDeepLinksProvider.deepLinks)
        }
}
