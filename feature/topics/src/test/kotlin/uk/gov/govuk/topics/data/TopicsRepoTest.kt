package uk.gov.govuk.topics.data

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import uk.gov.govuk.data.model.Result.DeviceOffline
import uk.gov.govuk.data.model.Result.Error
import uk.gov.govuk.data.model.Result.ServiceNotResponding
import uk.gov.govuk.data.model.Result.Success
import uk.gov.govuk.topics.data.local.TopicsLocalDataSource
import uk.gov.govuk.topics.data.local.model.LocalTopicItem
import uk.gov.govuk.topics.data.remote.TopicsApi
import uk.gov.govuk.topics.data.remote.model.RemoteTopic
import uk.gov.govuk.topics.data.remote.model.RemoteTopic.RemoteTopicContent
import uk.gov.govuk.topics.data.remote.model.RemoteTopicItem
import uk.gov.govuk.topics.domain.model.TopicItem
import java.io.IOException
import java.net.UnknownHostException

class TopicsRepoTest{

    private val topicsApi = mockk<TopicsApi>(relaxed = true)
    private val localDataSource = mockk<TopicsLocalDataSource>(relaxed = true)
    private val topicsResponse = mockk<Response<List<RemoteTopicItem>>>(relaxed = true)
    private val topicResponse = mockk<Response<RemoteTopic>>(relaxed = true)
    private val topic = mockk<RemoteTopic>(relaxed = true)

    @Test
    fun `Given the topics api response is unsuccessful, when sync, then do not sync local data source`() {
        coEvery { topicsApi.getTopics() } returns topicsResponse
        every { topicsResponse.isSuccessful } returns false

        val repo = TopicsRepo(topicsApi, localDataSource)

        runTest {
            repo.sync()
        }

        coVerify(exactly = 0) {
            localDataSource.sync(any())
        }
    }

    @Test
    fun `Given the topics api response body is empty, when sync, then do not sync local data source`() {
        coEvery { topicsApi.getTopics() } returns topicsResponse
        every { topicsResponse.isSuccessful } returns true
        every { topicsResponse.body() } returns null

        val repo = TopicsRepo(topicsApi, localDataSource)

        runTest {
            repo.sync()
        }

        coVerify(exactly = 0) {
            localDataSource.sync(any())
        }
    }

    @Test
    fun `Given the topics api throws an exception, when sync, then do not sync local data source`() {
        coEvery { topicsApi.getTopics() } throws IOException()

        val repo = TopicsRepo(topicsApi, localDataSource)

        runTest {
            repo.sync()
        }

        coVerify(exactly = 0) {
            localDataSource.sync(any())
        }
    }

    @Test
    fun `Given the topics api is successful, when sync, then sync local data source`() {
        val topics = listOf(
            RemoteTopicItem("ref", "title", "desc")
        )

        coEvery { topicsApi.getTopics() } returns topicsResponse
        every { topicsResponse.isSuccessful } returns true
        every { topicsResponse.body() } returns topics
        val repo = TopicsRepo(topicsApi, localDataSource)

        runTest {
            repo.sync()
        }

        coVerify {
            localDataSource.sync(topics)
        }
    }

    @Test
    fun `Given locally cached topics, when get topics, then emit topic items`() {
        every { localDataSource.topics } returns flowOf(
            listOf(
                LocalTopicItem().apply {
                    ref = "ref1"
                    title = "title1"
                    description = "desc1"
                    isSelected = true
                },
                LocalTopicItem().apply {
                    ref = "ref2"
                    title = "title2"
                    description = "desc2"
                    isSelected = false
                }
            )
        )

        val repo = TopicsRepo(topicsApi, localDataSource)

        runTest {
            val topics = repo.topics

            val expected = listOf(
                TopicItem("ref1", "title1", "desc1", true),
                TopicItem("ref2", "title2", "desc2", false),
            )

            assertEquals(expected, topics.first())
        }
    }

    @Test
    fun `Given a successful topic response with a body, then return success with topic and cache step by steps`() {
        val stepBySteps = listOf(
            RemoteTopicContent(
                url = "url-1",
                title = "title-1",
                isStepByStep = true,
                isPopular = false
            )
        )

        coEvery { topicsApi.getTopic("ref") } returns topicResponse
        coEvery { topicResponse.isSuccessful } returns true
        coEvery { topicResponse.body() } returns topic
        coEvery { topic.content } returns stepBySteps

        val repo = TopicsRepo(topicsApi, localDataSource)

        val expected = Success(topic)

        runTest {
            assertEquals(expected, repo.getTopic("ref"))
            assertEquals(stepBySteps, repo.stepBySteps)
        }
    }

    @Test
    fun `Given a successful topic response with a body, then return success with topic and cache popular pages`() {
        val popularPages = listOf(
            RemoteTopicContent(
                url = "url-1",
                title = "title-1",
                isStepByStep = false,
                isPopular = true
            )
        )

        coEvery { topicsApi.getTopic("ref") } returns topicResponse
        coEvery { topicResponse.isSuccessful } returns true
        coEvery { topicResponse.body() } returns topic
        coEvery { topic.content } returns popularPages

        val repo = TopicsRepo(topicsApi, localDataSource)

        val expected = Success(topic)

        runTest {
            assertEquals(expected, repo.getTopic("ref"))
            assertEquals(popularPages, repo.popularPages)
        }
    }

    @Test
    fun `Given the user it toggling the selection of a topic, when toggle selection, then update local data source`() {
        val repo = TopicsRepo(topicsApi, localDataSource)

        runTest {
            repo.toggleSelection("ref", true)

            coVerify{
                localDataSource.toggleSelection("ref", true)
            }
        }
    }

    @Test
    fun `Given topics are selected, when select all, then update local data source`() {
        val repo = TopicsRepo(topicsApi, localDataSource)

        runTest {
            repo.selectAll(listOf("ref1", "ref2"))

            coVerify{
                localDataSource.selectAll(listOf("ref1", "ref2"))
            }
        }
    }

    @Test
    fun `Given a successful topic response with an empty body, then return failure`() {
        coEvery { topicsApi.getTopic("ref") } returns topicResponse
        coEvery { topicResponse.isSuccessful } returns true
        coEvery { topicResponse.body() } returns null

        val repo = TopicsRepo(topicsApi, localDataSource)

        runTest {
            val result = repo.getTopic("ref")
            assertTrue(result is Error)
        }
    }

    @Test
    fun `Given an unsuccessful topic response, then return failure`() {
        coEvery { topicsApi.getTopic("ref") } returns topicResponse
        coEvery { topicResponse.isSuccessful } returns false

        val repo = TopicsRepo(topicsApi, localDataSource)

        runTest {
            val result = repo.getTopic("ref")
            assertTrue(result is Error)
        }
    }

    @Test
    fun `Get topic returns failure when the device is offline`() {
        coEvery { topicsApi.getTopic("ref") } throws UnknownHostException()

        val repo = TopicsRepo(topicsApi, localDataSource)

        runTest {
            val result = repo.getTopic("ref")
            assertTrue(result is DeviceOffline)
        }
    }

    @Test
    fun `Get topic returns failure when the Topic API is offline`() {
        val httpException = mockk<HttpException>(relaxed = true)
        coEvery { topicsApi.getTopic("ref") } throws httpException

        val repo = TopicsRepo(topicsApi, localDataSource)

        runTest {
            val result = repo.getTopic("ref")
            assertTrue(result is ServiceNotResponding)
        }
    }

    @Test
    fun `Get topic returns failure when any unknown error occurs`() {
        coEvery { topicsApi.getTopic("ref") } throws Exception()

        val repo = TopicsRepo(topicsApi, localDataSource)

        runTest {
            val result = repo.getTopic("ref")
            assertTrue(result is Error)
        }
    }

    @Test
    fun `Given topics are customised, when is topics customised, then return true`() {
        val repo = TopicsRepo(topicsApi, localDataSource)

        runTest {
            coEvery { localDataSource.isTopicsCustomised() } returns true

            assertTrue(repo.isTopicsCustomised())
        }
    }

    @Test
    fun `Given topics are not customised, when is topics customised, then return false`() {
        val repo = TopicsRepo(topicsApi, localDataSource)

        runTest {
            coEvery { localDataSource.isTopicsCustomised() } returns false

            assertFalse(repo.isTopicsCustomised())
        }
    }

    @Test
    fun `Given a user customises topics, when topics customised, then update local data source`() {
        val repo = TopicsRepo(topicsApi, localDataSource)

        runTest {
            repo.topicsCustomised()

            coVerify {
                localDataSource.topicsCustomised()
            }
        }
    }

    @Test
    fun `Given no topics, when has topics, then return false`() {
        coEvery { localDataSource.hasTopics() } returns false

        val repo = TopicsRepo(topicsApi, localDataSource)

        runTest {
            assertFalse(repo.hasTopics())
        }
    }

    @Test
    fun `Given topics, when has topics, then return true`() {
        coEvery { localDataSource.hasTopics() } returns true

        val repo = TopicsRepo(topicsApi, localDataSource)

        runTest {
            assertTrue(repo.hasTopics())
        }
    }

    @Test
    fun `Clear clears local data source`() {
        val repo = TopicsRepo(topicsApi, localDataSource)

        runTest {
            repo.clear()

            coVerify { localDataSource.clear() }
        }
    }
}
