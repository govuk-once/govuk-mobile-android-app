package uk.gov.govuk.chat

import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.chat.data.ChatRepo
import uk.gov.govuk.chat.data.local.ChatDataStore
import uk.gov.govuk.chat.data.remote.ChatResult
import uk.gov.govuk.chat.data.remote.model.Answer
import uk.gov.govuk.chat.data.remote.model.AnsweredQuestion
import uk.gov.govuk.chat.data.remote.model.Conversation
import uk.gov.govuk.chat.data.remote.model.PendingQuestion
import uk.gov.govuk.chat.data.remote.model.Source
import uk.gov.govuk.chat.domain.Analytics
import uk.gov.govuk.config.data.ConfigRepo
import uk.gov.govuk.data.auth.AuthRepo
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ChatViewModelTest {
    private val chatRepo = mockk<ChatRepo>(relaxed = true)
    private val chatDataStore = mockk<ChatDataStore>(relaxed = true)
    private val authRepo = mockk<AuthRepo>(relaxed = true)
    private val conversation = mockk<Conversation>(relaxed = true)
    private val pendingQuestion = mockk<PendingQuestion>(relaxed = true)
    private val question = mockk<AnsweredQuestion>(relaxed = true)
    private val analyticsClient = mockk<AnalyticsClient>(relaxed = true)
    private val configRepo = mockk<ConfigRepo>(relaxed = true)

    private val dispatcher = UnconfinedTestDispatcher()

    private lateinit var viewModel: ChatViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)

        // Default set up that works for most tests
        coEvery { chatDataStore.isChatIntroSeen() } returns true
        coEvery { chatRepo.getConversation() } returns null

        viewModel = ChatViewModel(chatRepo, chatDataStore, authRepo, analyticsClient, configRepo)

        clearAllMocks()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Init emits onboarding if not seen`() = runTest {
        coEvery { chatDataStore.isChatIntroSeen() } returns false

        viewModel = ChatViewModel(chatRepo, chatDataStore, authRepo, analyticsClient, configRepo)

        assertEquals(ChatUiState.Onboarding, viewModel.uiState.value)
    }

    @Test
    fun `Init does not emit conversation if there is no conversation`() = runTest {
        coEvery { chatDataStore.isChatIntroSeen() } returns true
        coEvery { chatRepo.getConversation() } returns null

        viewModel = ChatViewModel(chatRepo, chatDataStore, authRepo, analyticsClient, configRepo)

        val uiState = viewModel.uiState.value as ChatUiState.Default

        assertFalse(uiState.isLoading)
        assertTrue(uiState.chatEntries.isEmpty())

        coVerify(exactly = 0) {
            chatRepo.getAnswer(any(), any(), any())
        }
    }

    @Test
    fun `Load conversation emits conversation if there is a conversation`() = runTest {
        coEvery { chatDataStore.isChatIntroSeen() } returns true

        val conversation = Conversation(
            id = "123",
            answeredQuestions =
                listOf(
                    AnsweredQuestion(
                        "abc",
                        uk.gov.govuk.chat.data.remote.model.Answer(
                            "",
                            "",
                            "Answer 1",
                            listOf(
                                Source(
                                    "url",
                                    "title"
                                )
                            )
                        ),
                        "",
                        "",
                        "Question 1"

                    )
                ),
            "",
            null
        )

        coEvery { chatRepo.getConversation() } coAnswers {
            delay(100)
            ChatResult.Success(conversation)
        }

        viewModel = ChatViewModel(chatRepo, chatDataStore, authRepo, analyticsClient, configRepo)

        advanceUntilIdle()

        val uiState = viewModel.uiState.value as ChatUiState.Default
        val chatEntries = uiState.chatEntries
        val chatEntry = chatEntries["abc"]
        val question = chatEntry?.question
        val answer = chatEntry?.answer

        assertFalse(uiState.isLoading)
        assertEquals("Question 1", question)
        assertEquals("Answer 1", answer)
        assertEquals("[title](url)", chatEntry?.sources?.first())

        coVerify(exactly = 0) {
            chatRepo.getAnswer(any(), any(), any())
        }
    }

    @Test
    fun `Load conversation emits error`() = runTest {
        coEvery { chatDataStore.isChatIntroSeen() } returns true
        coEvery { chatRepo.getConversation() } coAnswers {
            delay(100)
            ChatResult.Error()
        }

        viewModel = ChatViewModel(chatRepo, chatDataStore, authRepo, analyticsClient, configRepo)

        advanceUntilIdle()

        assertEquals(ChatUiState.Error(false), viewModel.uiState.value)

        coVerify(exactly = 0) {
            chatRepo.getAnswer(any(), any(), any())
        }
    }

    @Test
    fun `Load conversation emits retryable error`() = runTest {
        coEvery { chatDataStore.isChatIntroSeen() } returns true
        coEvery { chatRepo.getConversation() } coAnswers {
            delay(100)
            ChatResult.NotFound()
        }

        viewModel = ChatViewModel(chatRepo, chatDataStore, authRepo, analyticsClient, configRepo)

        advanceUntilIdle()

        assertEquals(ChatUiState.Error(true), viewModel.uiState.value)

        coVerify(exactly = 0) {
            chatRepo.getAnswer(any(), any(), any())
        }
    }

    @Test
    fun `Load conversation emits auth error`() = runTest {
        coEvery { chatRepo.getConversation() } coAnswers {
            delay(100)
            ChatResult.AuthError()
        }

        var authError = false
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.authError.collect {
                authError = true
            }
        }

        viewModel.loadConversation()

        advanceUntilIdle()

        assertTrue(authError)
    }

    @Test
    fun `Load conversation gets answer if there is a pending question`() = runTest {
        coEvery { chatDataStore.isChatIntroSeen() } returns true
        coEvery { chatRepo.getConversation() } coAnswers {
            delay(100)
            ChatResult.Success(conversation)
        }

        every { conversation.pendingQuestion } returns pendingQuestion

        viewModel = ChatViewModel(chatRepo, chatDataStore, authRepo, analyticsClient, configRepo)

        advanceUntilIdle()

        coVerify {
            chatRepo.getAnswer(any(), any())
        }
    }

    @Test
    fun `Clear conversation`() = runTest {
        val uiStates = mutableListOf<ChatUiState?>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.toList(uiStates)
        }

        viewModel.clearConversation()

        advanceUntilIdle()

        val uiState = viewModel.uiState.value as ChatUiState.Default

        assertFalse(uiState.isLoading)
        assertTrue(uiState.chatEntries.isEmpty())
    }

    @Test
    fun `On Submit emits PII error`() = runTest {
        viewModel.onSubmit("test@email.com")

        val uiState = viewModel.uiState.value as ChatUiState.Default

        assertFalse(uiState.isLoading)
        assertTrue(uiState.isPiiError)

        coVerify(exactly = 0) {
            chatRepo.askQuestion(any())
            chatRepo.getAnswer(any(), any(), any())
        }
    }

    @Test
    fun `Submit emits question`() = runTest {
        val question = AnsweredQuestion(
            "abc",
            Answer(
                "",
                "",
                "",
                null
            ),
            "",
            "",
            "Question 1"
        )


        coEvery { chatRepo.askQuestion(any()) } coAnswers {
            delay(100)
            ChatResult.Success(question)
        }

        val uiStates = mutableListOf<ChatUiState?>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.toList(uiStates)
        }

        viewModel.onSubmit("First Question")

        advanceUntilIdle()

        val loadingState = uiStates[1] as ChatUiState.Default

        assertTrue(loadingState.isLoading)
        assertEquals(0, loadingState.chatEntries.size)

        val finalState = uiStates.last() as ChatUiState.Default
        val chatEntries = finalState.chatEntries
        val chatEntry = chatEntries["abc"]

        assertEquals("Question 1", chatEntry?.question)
    }

    @Test
    fun `Submit emits answer`() = runTest {
        val question = AnsweredQuestion(
            "abc",
            Answer(
                "",
                "",
                "",
                null
            ),
            "",
            "",
            "Question 1"
        )

        val answer = Answer(
            "",
            "",
            "Answer 1",
            listOf(
                Source(
                    "url",
                    "title"
                )
            )
        )

        coEvery { chatRepo.askQuestion(any()) } coAnswers {
            delay(100)
            ChatResult.Success(question)
        }

        coEvery { chatRepo.getAnswer(any(), any()) } returns ChatResult.Success(answer)

        val uiStates = mutableListOf<ChatUiState?>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.toList(uiStates)
        }

        viewModel.onSubmit("First Question")

        advanceUntilIdle()

        val loadingState = uiStates[1] as ChatUiState.Default

        assertTrue(loadingState.isLoading)
        assertEquals(0, loadingState.chatEntries.size)

        val finalState = uiStates.last() as ChatUiState.Default
        val chatEntries = finalState.chatEntries
        val chatEntry = chatEntries["abc"]

        assertFalse(finalState.isLoading)
        assertEquals("Question 1", chatEntry?.question)
        assertEquals("Answer 1", chatEntry?.answer)
        assertEquals("[title](url)", chatEntry?.sources?.first())
    }

    @Test
    fun `Submit emits question PII error`() = runTest {
        coEvery { chatRepo.askQuestion(any()) } coAnswers {
            delay(100)
            ChatResult.ValidationError()
        }

        coEvery { chatDataStore.isChatIntroSeen() } returns true

        val uiStates = mutableListOf<ChatUiState?>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.toList(uiStates)
        }

        viewModel.onSubmit("First Question")

        advanceUntilIdle()

        val loadingState = uiStates[1] as ChatUiState.Default

        assertTrue(loadingState.isLoading)
        assertEquals(0, loadingState.chatEntries.size)

        val finalState = uiStates.last() as ChatUiState.Default

        assertFalse(finalState.isLoading)
        assertTrue(finalState.chatEntries.isEmpty())
        assertTrue(finalState.isPiiError)

        coVerify(exactly = 0) {
            chatRepo.getAnswer(any(), any(), any())
        }
    }

    @Test
    fun `Submit emits question error`() = runTest {
        coEvery { chatRepo.askQuestion(any()) } coAnswers {
            delay(100)
            ChatResult.Error()
        }

        val uiStates = mutableListOf<ChatUiState?>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.toList(uiStates)
        }

        viewModel.onSubmit("First Question")

        advanceUntilIdle()

        val loadingState = uiStates[1] as ChatUiState.Default

        assertTrue(loadingState.isLoading)
        assertEquals(0, loadingState.chatEntries.size)

        val finalState = uiStates.last()

        assertEquals(ChatUiState.Error(false),finalState)

        coVerify(exactly = 0) {
            chatRepo.getAnswer(any(), any(), any())
        }
    }

    @Test
    fun `Submit emits auth error`() = runTest {
        coEvery { chatRepo.askQuestion(any()) } coAnswers {
            delay(100)
            ChatResult.AuthError()
        }

        var authError = false
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.authError.collect {
                authError = true
            }
        }

        viewModel.onSubmit("Question")

        advanceUntilIdle()

        assertTrue(authError)
    }

    @Test
    fun `Submit emits answer error`() = runTest {
        coEvery { chatRepo.askQuestion(any()) } coAnswers {
            delay(100)
            ChatResult.Success(question)
        }

        coEvery { chatRepo.getAnswer(any(), any(), any()) } returns ChatResult.Error()

        val uiStates = mutableListOf<ChatUiState?>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.toList(uiStates)
        }

        viewModel.onSubmit("First Question")

        advanceUntilIdle()

        val loadingState = uiStates[1] as ChatUiState.Default

        assertTrue(loadingState.isLoading)
        assertEquals(0, loadingState.chatEntries.size)

        val finalState = uiStates.last()

        assertEquals(ChatUiState.Error(false), finalState)
    }

    @Test
    fun `Submit emits answer auth error`() = runTest {
        coEvery { chatRepo.askQuestion(any()) } coAnswers {
            delay(100)
            ChatResult.Success(question)
        }

        coEvery { chatRepo.getAnswer(any(), any(), any()) } returns ChatResult.AuthError()

        var authError = false
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.authError.collect {
                authError = true
            }
        }

        viewModel.onSubmit("Question")

        advanceUntilIdle()

        assertTrue(authError)
    }

    @Test
    fun `Question updated emits no errors or warnings`() = runTest {
        viewModel.onQuestionUpdated("abc", 10, 5)

        val uiState = viewModel.uiState.value as ChatUiState.Default

        assertEquals("abc", uiState.question)
        assertFalse(uiState.isPiiError)
        assertFalse(uiState.displayCharacterWarning)
        assertFalse(uiState.displayCharacterError)
        assertEquals(7, uiState.charactersRemaining)
    }

    @Test
    fun `Question updated emits character warning`() = runTest {
        viewModel.onQuestionUpdated("abcde", 10, 5)

        val uiState = viewModel.uiState.value as ChatUiState.Default

        assertEquals("abcde", uiState.question)
        assertFalse(uiState.isPiiError)
        assertTrue(uiState.displayCharacterWarning)
        assertFalse(uiState.displayCharacterError)
        assertEquals(5, uiState.charactersRemaining)
    }

    @Test
    fun `Question updated emits character error and submit disabled`() = runTest {
        viewModel.onQuestionUpdated("abcdefghjkl", 10, 5)

        val uiState = viewModel.uiState.value as ChatUiState.Default

        assertEquals("abcdefghjkl", uiState.question)
        assertFalse(uiState.isPiiError)
        assertFalse(uiState.displayCharacterWarning)
        assertTrue(uiState.displayCharacterError)
        assertEquals(-1, uiState.charactersRemaining)
    }

    @Test
    fun `Question updated emits submit disabled when question is blank`() = runTest {
        viewModel.onQuestionUpdated(" ", 5, 3)

        val uiState = viewModel.uiState.value as ChatUiState.Default

        assertEquals(" ", uiState.question)
        assertFalse(uiState.isPiiError)
        assertFalse(uiState.displayCharacterWarning)
        assertFalse(uiState.displayCharacterError)
        assertEquals(4, uiState.charactersRemaining)
    }

    @Test
    fun `setChatIntroSeen calls datastore and updates uiState`() = runTest {
        coEvery { chatDataStore.isChatIntroSeen() } coAnswers { true }

        val uiStates = mutableListOf<ChatUiState?>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.toList(uiStates)
        }

        viewModel.setChatIntroSeen()
        advanceUntilIdle()

        coVerify(exactly = 1) { chatDataStore.saveChatIntroSeen() }

        assertEquals(ChatUiState.Default(), viewModel.uiState.value)
    }

    @Test
    fun `Given a page view, then log analytics`() {
        val screenClass = "screenClass"
        val screenName = "screenName"
        val title = "title"

        viewModel.onPageView(
            screenClass = screenClass,
            screenName = screenName,
            title = title
        )

        verify {
            analyticsClient.screenView(
                screenClass = screenClass,
                screenName = screenName,
                title = title
            )
        }
    }

    @Test
    fun `When the user clicks an action menu item, then log analytics`() {
        val text = "text"
        val section = "section"
        val action = "action"

        viewModel.onFunctionActionItemClicked(
            text = text,
            section = section,
            action = action
        )

        verify {
            analyticsClient.buttonFunction(
                text = text,
                section = section,
                action = action
            )
        }
    }

    @Test
    fun `When the user clicks a navigation action item, then log analytics`() {
        val buttonText = "buttonText"
        val url = "url"

        viewModel.onNavigationActionItemClicked(buttonText, url)

        verify {
            analyticsClient.buttonClick(
                text = buttonText,
                url = url,
                external = true
            )
        }
    }

    @Test
    fun `When the user enters a question and submits it, then log analytics`() {
        viewModel.onQuestionSubmit()

        verify {
            analyticsClient.chat()
        }
    }

    @Test
    fun `Given an onboarding screen button click, then log analytics`() {
        val text = "text"
        val section = "section"

        viewModel.onButtonClicked(
            text = text,
            section = section
        )

        verify {
            analyticsClient.buttonClick(
                text = text,
                section = section
            )
        }
    }

    @Test
    fun `Given an onboarding screen markdown link click, then log analytics`() {
        val text = "text"
        val url = "url"

        viewModel.onMarkdownLinkClicked(
            text = text,
            url = url
        )

        verify {
            analyticsClient.chatMarkdownLinkClick(
                text = text,
                url = url
            )
        }
    }

    @Test
    fun `Given the sources are expanded, then log analytics`() {
        viewModel.onSourcesExpanded()

        verify {
            analyticsClient.buttonFunction(
                text = Analytics.RESPONSE_SOURCE_LINKS_OPENED,
                section = Analytics.RESPONSE,
                action = Analytics.RESPONSE_SOURCE_LINKS_OPENED
            )
        }
    }
}
