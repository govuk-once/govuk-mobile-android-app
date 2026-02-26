package uk.gov.govuk.topics

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.topics.data.TopicsRepo

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
}