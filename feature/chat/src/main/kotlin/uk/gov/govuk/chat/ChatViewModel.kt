package uk.gov.govuk.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.chat.ChatUiState.Default
import uk.gov.govuk.chat.ChatUiState.Error
import uk.gov.govuk.chat.ChatUiState.Onboarding
import uk.gov.govuk.chat.data.ChatRepo
import uk.gov.govuk.chat.data.local.ChatDataStore
import uk.gov.govuk.chat.data.remote.ChatResult
import uk.gov.govuk.chat.data.remote.ChatResult.AuthError
import uk.gov.govuk.chat.data.remote.ChatResult.NotFound
import uk.gov.govuk.chat.data.remote.ChatResult.Success
import uk.gov.govuk.chat.data.remote.ChatResult.ValidationError
import uk.gov.govuk.chat.data.remote.model.Answer
import uk.gov.govuk.chat.domain.Analytics
import uk.gov.govuk.chat.domain.StringCleaner
import uk.gov.govuk.chat.ui.model.ChatEntry
import uk.gov.govuk.config.data.ConfigRepo
import uk.gov.govuk.data.auth.AuthRepo
import javax.inject.Inject

internal sealed class ChatUiState {
    data object Onboarding: ChatUiState()
    data class Error(val canRetry: Boolean): ChatUiState()
    data class Default(
        val question: String = "",
        val chatEntries: LinkedHashMap<String, ChatEntry> = linkedMapOf(),
        val isLoading: Boolean = false,
        val isPiiError: Boolean = false,
        val displayCharacterWarning: Boolean = false,
        val displayCharacterError: Boolean = false,
        val charactersRemaining: Int = 0
    ): ChatUiState()
}

@HiltViewModel
internal class ChatViewModel @Inject constructor(
    private val chatRepo: ChatRepo,
    private val chatDataStore: ChatDataStore,
    private val authRepo: AuthRepo,
    private val analyticsClient: AnalyticsClient,
    configRepo: ConfigRepo
): ViewModel() {

    private val _uiState: MutableStateFlow<ChatUiState?> = MutableStateFlow(null)
    val uiState = _uiState.asStateFlow()

    private val _authError = MutableSharedFlow<Unit>()
    val authError: SharedFlow<Unit> = _authError

    val chatUrls = configRepo.chatUrls

    init {
        viewModelScope.launch {
            if (chatDataStore.isChatIntroSeen()) {
                loadConversation()
            } else {
                _uiState.value = Onboarding
            }
        }
    }

    fun loadConversation() {
        viewModelScope.launch {
            chatRepo.getConversation()?.let { result ->
                handleChatResult(result) { conversation ->
                    _uiState.value = Default(
                        chatEntries = conversation.answeredQuestions.mapNotNull { question ->
                            question.answer?.let { answer ->
                                question.id to ChatEntry(
                                    question = question.message,
                                    answer = answer.message,
                                    sources = answer.sources?.map { source ->
                                        "[${source.title}](${source.url})"
                                    },
                                    shouldAnimate = false
                                )
                            }
                        }.toMap(LinkedHashMap())
                    )

                    conversation.pendingQuestion?.let { pendingQuestion ->
                        _uiState.updateDefault { it.copy(isLoading = true) }

                        addChatEntry(
                            questionId = pendingQuestion.id,
                            question = pendingQuestion.message
                        )
                        getAnswer(
                            conversationId = conversation.id,
                            questionId = pendingQuestion.id
                        )
                    }
                }
            } ?: run {
                _uiState.value = Default()
            }
        }
    }

    fun clearConversation() {
        viewModelScope.launch {
            _uiState.value = Default(isLoading = true)
            chatRepo.clearConversation()
            _uiState.updateDefault { it.copy(isLoading = false) }
        }
    }

    fun setChatIntroSeen() {
        viewModelScope.launch {
            chatDataStore.saveChatIntroSeen()
            _uiState.value = Default()
        }
    }

    fun onSubmit(question: String) {
        val isPiiError = StringCleaner.includesPII(question)
        _uiState.updateDefault { it.copy(isPiiError = isPiiError) }

        if (!isPiiError) {
            viewModelScope.launch {
                _uiState.updateDefault { it.copy(isLoading = true) }

                handleChatResult(chatRepo.askQuestion(question)) { answeredQuestion ->
                    addChatEntry(
                        questionId = answeredQuestion.id,
                        question = answeredQuestion.message
                    )
                    onQuestionUpdated("")
                    getAnswer(
                        conversationId = answeredQuestion.conversationId,
                        questionId = answeredQuestion.id
                    )
                }
            }
        }
    }

    fun onQuestionUpdated(
        question: String,
        characterLimit: Int = 300,
        characterWarningThreshold: Int = 50
    ) {
        val remainingCharacters = characterLimit - question.length
        val displayCharacterError = remainingCharacters < 0

        _uiState.updateDefault {
            it.copy(
                question = question,
                isPiiError = false,
                displayCharacterWarning = remainingCharacters in 0..characterWarningThreshold,
                displayCharacterError = displayCharacterError,
                charactersRemaining = characterLimit - question.length
            )
        }
    }

    fun onPageView(screenClass: String, screenName: String, title: String) {
        analyticsClient.screenView(
            screenClass = screenClass,
            screenName = screenName,
            title = title
        )
    }

    fun onNavigationActionItemClicked(text: String, url: String) {
        analyticsClient.buttonClick(
            text = text,
            url = url,
            external = true
        )
    }

    fun onFunctionActionItemClicked(text: String, section: String, action: String) {
        analyticsClient.buttonFunction(
            text = text,
            section = section,
            action = action
        )
    }

    fun onQuestionSubmit() {
        analyticsClient.chat()
    }

    fun onButtonClicked(text: String, section: String) {
        analyticsClient.buttonClick(
            text = text,
            section = section
        )
    }

    fun onMarkdownLinkClicked(text: String, url: String) {
        analyticsClient.chatMarkdownLinkClick(text = text, url = url)
    }

    fun onSourcesExpanded() {
        analyticsClient.buttonFunction(
            text = Analytics.RESPONSE_SOURCE_LINKS_OPENED,
            section = Analytics.RESPONSE,
            action = Analytics.RESPONSE_SOURCE_LINKS_OPENED
        )
    }

    private suspend fun getAnswer(conversationId: String, questionId: String) {
        handleChatResult(
            chatRepo.getAnswer(
                conversationId = conversationId,
                questionId = questionId
            )
        ) { answer ->
            updateChatEntry(questionId, answer)
            _uiState.updateDefault { it.copy(isLoading = false) }
            analyticsClient.chatQuestionAnswerReturnedEvent()
        }
    }

    private suspend fun <T> handleChatResult(chatResult: ChatResult<T>, onSuccess: suspend (T) -> Unit) {
        when (chatResult) {
            is Success -> onSuccess(chatResult.value)
            is ValidationError -> _uiState.updateDefault {
                it.copy(isLoading = false, isPiiError = true)
            }
            is NotFound -> _uiState.value = Error(canRetry = true)
            is AuthError -> {
                authRepo.clear()
                _authError.emit(Unit)
            }
            else -> _uiState.value = Error(canRetry = false)
        }
    }

    private fun addChatEntry(
        questionId: String,
        question: String
    ) {
        _uiState.updateDefault {
            it.copy(
                chatEntries = LinkedHashMap(it.chatEntries).apply {
                    put(
                        questionId,
                        ChatEntry(
                            question = question,
                            answer = "",
                            sources = emptyList()
                        )
                    )
                }
            )
        }
    }

    private fun updateChatEntry(questionId: String, answer: Answer?) {
        if (answer != null) {
            (_uiState.value as Default).chatEntries[questionId]?.let { chatEntry ->
                chatEntry.answer = answer.message
                chatEntry.sources = answer.sources?.map { source ->
                    "[${source.title}](${source.url})"
                }
            }
        }
    }

    private inline fun MutableStateFlow<ChatUiState?>.updateDefault(
        transform: (Default) -> Default
    ) {
        update { state -> transform(state as Default) }
    }

}
