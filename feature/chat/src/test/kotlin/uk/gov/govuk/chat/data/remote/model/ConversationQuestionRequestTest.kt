package uk.gov.govuk.chat.data.remote.model

import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Test

class ConversationQuestionRequestTest {

    private val gson = Gson()

    @Test
    fun `Serializes suggested prompt when true`() {
        val request = ConversationQuestionRequest("question", suggestedPrompt = true)

        assertEquals("""{"user_question":"question","suggested_prompt":true}""", gson.toJson(request))
    }

    @Test
    fun `Omits suggested prompt when null`() {
        val request = ConversationQuestionRequest("question")

        assertEquals("""{"user_question":"question"}""", gson.toJson(request))
    }
}
