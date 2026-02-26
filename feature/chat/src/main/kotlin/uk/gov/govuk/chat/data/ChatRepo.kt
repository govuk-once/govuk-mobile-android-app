package uk.gov.govuk.chat.data

import kotlinx.coroutines.delay
import uk.gov.govuk.chat.data.local.ChatDataStore
import uk.gov.govuk.chat.data.remote.ChatApi
import uk.gov.govuk.chat.data.remote.ChatResult
import uk.gov.govuk.chat.data.remote.ChatResult.AwaitingAnswer
import uk.gov.govuk.chat.data.remote.ChatResult.Success
import uk.gov.govuk.chat.data.remote.model.Answer
import uk.gov.govuk.chat.data.remote.model.AnsweredQuestion
import uk.gov.govuk.chat.data.remote.model.Conversation
import uk.gov.govuk.chat.data.remote.model.ConversationQuestionRequest
import uk.gov.govuk.chat.data.remote.safeChatApiCall
import uk.gov.govuk.config.data.ConfigRepo
import uk.gov.govuk.data.auth.AuthRepo
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.seconds

@Singleton
internal class ChatRepo @Inject constructor(
    private val chatApi: ChatApi,
    private val dataStore: ChatDataStore,
    private val configRepo: ConfigRepo,
    private val authRepo: AuthRepo
) {

    suspend fun getConversation(): ChatResult<Conversation>? {
        val conversationId = dataStore.conversationId()
        return if (conversationId != null) {
            safeChatApiCall(
                apiCall = { chatApi.getConversation(conversationId) },
                authRepo = authRepo
            )
        } else {
            null
        }
    }

    suspend fun askQuestion(question: String): ChatResult<AnsweredQuestion> {
        val conversationId = dataStore.conversationId()
        return if (conversationId != null) {
            updateConversation(
                conversationId = conversationId,
                question = question
            )
        } else {
            startConversation(question)
        }
    }

    private suspend fun startConversation(question: String): ChatResult<AnsweredQuestion> {
        val requestBody = ConversationQuestionRequest(
            userQuestion = question
        )

        val result = safeChatApiCall(
            apiCall = { chatApi.startConversation(requestBody) },
            authRepo = authRepo
        )
        if (result is Success) {
            dataStore.saveConversationId(result.value.conversationId)
        }
        return result
    }

    private suspend fun updateConversation(conversationId: String, question: String): ChatResult<AnsweredQuestion> {
        val requestBody = ConversationQuestionRequest(
            userQuestion = question
        )

        return safeChatApiCall(
            apiCall = { chatApi.updateConversation(conversationId, requestBody) },
            authRepo = authRepo
        )
    }

    suspend fun getAnswer(
        conversationId: String,
        questionId: String,
        wait: Double? = null,
    ): ChatResult<Answer> {
        val pollInterval = wait ?: configRepo.chatPollIntervalSeconds

        while (true) {
            delay(pollInterval.seconds)

            val result = safeChatApiCall(
                apiCall = { chatApi.getAnswer(conversationId, questionId) },
                authRepo = authRepo
            )

            if (result !is AwaitingAnswer) {
                return result
            }
        }
    }

    suspend fun clearConversation() {
        dataStore.clearConversation()
    }

    suspend fun clear() {
        dataStore.clear()
    }
}
