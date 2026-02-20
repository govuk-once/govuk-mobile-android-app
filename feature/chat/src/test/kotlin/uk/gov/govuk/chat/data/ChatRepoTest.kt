package uk.gov.govuk.chat.data

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import uk.gov.govuk.chat.data.local.ChatDataStore
import uk.gov.govuk.chat.data.remote.ChatApi
import uk.gov.govuk.chat.data.remote.ChatResult.NotFound
import uk.gov.govuk.chat.data.remote.ChatResult.Success
import uk.gov.govuk.chat.data.remote.model.Answer
import uk.gov.govuk.chat.data.remote.model.AnsweredQuestion
import uk.gov.govuk.chat.data.remote.model.ConversationQuestionRequest
import uk.gov.govuk.config.data.ConfigRepo
import uk.gov.govuk.data.auth.AuthRepo
import kotlin.test.assertTrue

class ChatRepoTest {

    private val chatApi = mockk<ChatApi>(relaxed = true)
    private val dataStore = mockk<ChatDataStore>(relaxed = true)
    private val configRepo = mockk<ConfigRepo>(relaxed = true)
    private val authRepo = mockk<AuthRepo>(relaxed = true)
    private val questionResponse = mockk<Response<AnsweredQuestion>>(relaxed = true)
    private val answeredQuestion = mockk<AnsweredQuestion>(relaxed = true)
    private val answerResponse = mockk<Response<Answer>>(relaxed = true)
    private val answer = mockk<Answer>(relaxed = true)

    private lateinit var chatRepo: ChatRepo

    @Before
    fun setup() {
        chatRepo = ChatRepo(chatApi, dataStore, configRepo, authRepo)
    }

    @Test
    fun `Get conversation returns null when there is no conversation id`() = runTest {
        coEvery { dataStore.conversationId() } returns null

        assertNull(chatRepo.getConversation())
    }

    @Test
    fun `Get conversation performs API call when there is a conversation id`() = runTest {
        coEvery { dataStore.conversationId() } returns "123"

        chatRepo.getConversation()

        coVerify {
            chatApi.getConversation("123")
        }
    }

    @Test
    fun `Ask question updates existing conversation`() = runTest {
        coEvery { dataStore.conversationId() } returns "123"

        chatRepo.askQuestion("question")

        coVerify {
            chatApi.updateConversation(
                "123",
                ConversationQuestionRequest("question")
            )
        }

        coVerify(exactly = 0) {
            chatApi.startConversation(any())
        }
    }

    @Test
    fun `Ask question creates a new conversation and persists id`() = runTest {
        coEvery { dataStore.conversationId() } returns null
        coEvery { chatApi.startConversation(any()) } returns questionResponse
        coEvery { questionResponse.isSuccessful } returns true
        coEvery { questionResponse.body() } returns answeredQuestion
        coEvery { answeredQuestion.conversationId } returns "123"

        chatRepo.askQuestion("question")

        coVerify {
            chatApi.startConversation(
                ConversationQuestionRequest("question")
            )
            dataStore.saveConversationId("123")
        }

        coVerify(exactly = 0) {
            chatApi.updateConversation(any(), any())
        }
    }

    @Test
    fun `Get answer retries and returns answer when api returns an answer`() = runTest {
        coEvery { chatApi.getAnswer(any(), any()) } returns answerResponse
        every { answerResponse.isSuccessful } returns true
        every { answerResponse.code() } returns 202 andThen 202 andThen 200
        every { answerResponse.body() } returns answer

        val expected = Success(answer)

        assertEquals(expected, chatRepo.getAnswer("123", "abc", wait = 1.0))
    }

    @Test
    fun `Get answer retries and returns error when api returns an error`() = runTest {
        coEvery { chatApi.getAnswer(any(), any()) } returns answerResponse
        every { answerResponse.isSuccessful } returns true andThen false
        every { answerResponse.code() } returns 202 andThen 202 andThen 404

        val result = chatRepo.getAnswer("123", "abc", wait = 1.0)

        assertTrue(result is NotFound)
    }

    @Test
    fun `Given a conversation id, when cleared the conversation id is removed`() = runTest {
        coEvery { dataStore.conversationId() } returns "123"

        chatRepo.getConversation()

        coVerify {
            chatApi.getConversation("123")
        }

        chatRepo.clearConversation()

        coVerify {
            dataStore.clearConversation()
        }

        coEvery { dataStore.conversationId() } returns null

        assertNull(chatRepo.getConversation())
    }

    @Test
    fun `Clear the data store`() = runTest {
        chatRepo.clear()

        coVerify {
            dataStore.clear()
        }
    }
}
