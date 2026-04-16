package uk.gov.govuk.topics.data.local

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.topics.data.local.model.LocalTopicItemEntity
import uk.gov.govuk.topics.data.remote.model.RemoteTopicItem

class TopicsLocalDataSourceTest {

    private val dao = mockk<TopicsDao>(relaxed = true)
    private val dataStore = mockk<TopicsDataStore>(relaxed = true)
    private lateinit var dataSource: TopicsLocalDataSource

    @Before
    fun setup() {
        dataSource = TopicsLocalDataSource(dao, dataStore)
    }

    @Test
    fun `Given topic items in db, when get topics, then emit topic items`() {
        val items = listOf(
            LocalTopicItemEntity(ref = "ref1", title = "title1", description = "desc1", isSelected = true),
            LocalTopicItemEntity(ref = "ref2", title = "title2", description = "desc2", isSelected = false)
        )
        every { dao.getTopics() } returns flowOf(items)

        runTest {
            val topics = dataSource.topics.first()
            assertEquals(2, topics.size)
            assertEquals("ref1", topics[0].ref)
            assertTrue(topics[0].isSelected)
            assertEquals("ref2", topics[1].ref)
            assertFalse(topics[1].isSelected)
        }
    }

    @Test
    fun `Given new remote topics, when topics are synced, then insert new topics as not selected`() {
        val remoteTopics = listOf(RemoteTopicItem(ref = "ref1", title = "title1", description = "desc1"))
        coEvery { dao.getAllRefs() } returns emptyList()

        runTest {
            dataSource.sync(remoteTopics)
            coVerify { dao.insert(match { it.ref == "ref1" && !it.isSelected }) }
        }
    }

    @Test
    fun `Given a topic is not present in remote topics, when topics are synced, then delete the topic`() {
        coEvery { dao.getAllRefs() } returns listOf("ref1")

        runTest {
            dataSource.sync(emptyList())
            coVerify { dao.delete("ref1") }
        }
    }

    @Test
    fun `Given a topic is updated and topics have not been customised, when topics are synced, then update title and description and clear selection`() {
        val remoteTopics = listOf(RemoteTopicItem(ref = "ref1", title = "title2", description = "desc2"))
        coEvery { dao.getAllRefs() } returns listOf("ref1")
        coEvery { dataStore.isTopicsCustomised() } returns false

        runTest {
            dataSource.sync(remoteTopics, UnconfinedTestDispatcher())
            coVerify { dao.updateTitleDescriptionAndClearSelection("ref1", "title2", "desc2") }
            coVerify(exactly = 0) { dao.updateTitleAndDescription(any(), any(), any()) }
        }
    }

    @Test
    fun `Given a topic is updated and topics have been customised, when topics are synced, then update title and description and maintain selection`() {
        val remoteTopics = listOf(RemoteTopicItem(ref = "ref1", title = "title2", description = "desc2"))
        coEvery { dao.getAllRefs() } returns listOf("ref1")
        coEvery { dataStore.isTopicsCustomised() } returns true

        runTest {
            dataSource.sync(remoteTopics, UnconfinedTestDispatcher())
            coVerify { dao.updateTitleAndDescription("ref1", "title2", "desc2") }
            coVerify(exactly = 0) { dao.updateTitleDescriptionAndClearSelection(any(), any(), any()) }
        }
    }

    @Test
    fun `Given a topic is selected, then update the topic in db`() {
        runTest {
            dataSource.toggleSelection("ref1", true)
            coVerify { dao.toggleSelection("ref1", true) }
        }
    }

    @Test
    fun `Given a topic is deselected, then update the topic in db`() {
        runTest {
            dataSource.toggleSelection("ref1", false)
            coVerify { dao.toggleSelection("ref1", false) }
        }
    }

    @Test
    fun `Given multiple topics are selected, then update the topics in db`() {
        runTest {
            dataSource.selectAll(listOf("ref1", "ref2"))
            coVerify { dao.selectAll(listOf("ref1", "ref2")) }
        }
    }

    @Test
    fun `Given topics are customised, when is topics customised, then return true`() {
        coEvery { dataStore.isTopicsCustomised() } returns true

        runTest { assertTrue(dataSource.isTopicsCustomised()) }
    }

    @Test
    fun `Given topics are not customised, when is topics customised, then return false`() {
        coEvery { dataStore.isTopicsCustomised() } returns false

        runTest { assertFalse(dataSource.isTopicsCustomised()) }
    }

    @Test
    fun `Given a user customises topics, when topics customised, then update data store`() {
        runTest {
            dataSource.topicsCustomised()
            coVerify { dataStore.topicsCustomised() }
        }
    }

    @Test
    fun `Given topics, when has topics, then return true`() {
        coEvery { dao.count() } returns 1

        runTest { assertTrue(dataSource.hasTopics()) }
    }

    @Test
    fun `Given no topics, when has topics, then return false`() {
        coEvery { dao.count() } returns 0

        runTest { assertFalse(dataSource.hasTopics()) }
    }

    @Test
    fun `Given topics, when clear, then clear selections in db and clear data store`() {
        runTest {
            dataSource.clear()
            coVerify { dao.clearAllSelections() }
            coVerify { dataStore.clear() }
        }
    }
}
