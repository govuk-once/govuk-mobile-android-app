package uk.gov.govuk.chat.data.remote.model

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.junit.Assert.assertEquals
import org.junit.Test

class ConversationQuestionRequestTest {

    private val gson = Gson()
    private val gsonSerializingNulls = GsonBuilder().serializeNulls().create()

    @Test
    fun `Serializes suggested prompt when true`() {
        val request = ConversationQuestionRequest("question", isSuggestedPrompt = true)

        assertEquals("""{"user_question":"question","suggested_prompt":true}""", gson.toJson(request))
    }

    @Test
    fun `Omits suggested prompt when false`() {
        val request = ConversationQuestionRequest("question")

        assertEquals("""{"user_question":"question"}""", gson.toJson(request))
    }

    @Test
    fun `Omits suggested prompt when false even if Gson serializes nulls`() {
        val request = ConversationQuestionRequest("question")

        assertEquals("""{"user_question":"question"}""", gsonSerializingNulls.toJson(request))
    }
}
