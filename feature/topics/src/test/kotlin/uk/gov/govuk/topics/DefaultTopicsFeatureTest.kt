package uk.gov.govuk.topics

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.topics.data.TopicsRepo
import uk.gov.govuk.topics.domain.model.TopicItem

class DefaultTopicsFeatureTest {

    private val topicsRepo = mockk<TopicsRepo>(relaxed = true)

    private lateinit var topicsFeature: DefaultTopicsFeature

    @Before
    fun setup() {
        topicsFeature = DefaultTopicsFeature(topicsRepo)
    }

    @Test
    fun `Init syncs topics repo`() {
        runTest {
            topicsFeature.init()

            coVerify {
                topicsRepo.sync()
            }
        }
    }

    @Test
    fun `Clear clears topics repo`() {
        runTest {
            topicsFeature.clear()

            coVerify {
                topicsRepo.clear()
            }
        }
    }

    @Test
    fun `Has topics returns false`() {
        coEvery { topicsRepo.hasTopics() } returns false

        runTest {
            assertFalse(topicsFeature.hasTopics())
        }
    }

    @Test
    fun `Has topics returns true`() {
        coEvery { topicsRepo.hasTopics() } returns true

        runTest {
            assertTrue(topicsFeature.hasTopics())
        }
    }

    @Test
    fun `Given init, when topics is null, then topics references is null`() {
        coEvery { topicsRepo.topics.firstOrNull() } returns null

        runTest {
            topicsFeature.init()

            assertNull(topicsFeature.topicsReferences)
        }
    }

    @Test
    fun `Given init, when topics has a value, then topics references has the value`() {
        val topics = listOf(
            TopicItem(
                ref = "ref",
                title = "title",
                description = "description",
                isSelected = false
            )
        )
        every { topicsRepo.topics } returns flowOf(topics)

        runTest {
            topicsFeature.init()

            assertEquals(listOf("ref"), topicsFeature.topicsReferences)
        }
    }
}
