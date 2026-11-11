package uk.gov.govuk.chat.data.remote.model

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.google.gson.annotations.JsonAdapter
import java.lang.reflect.Type

@JsonAdapter(ConversationQuestionRequest.Serializer::class)
data class ConversationQuestionRequest(
    val userQuestion: String,
    val isSuggestedPrompt: Boolean = false
) {
    // suggested_prompt is only included when true, regardless of the Gson null serialization config
    class Serializer : JsonSerializer<ConversationQuestionRequest> {
        override fun serialize(
            src: ConversationQuestionRequest,
            typeOfSrc: Type,
            context: JsonSerializationContext
        ): JsonElement = JsonObject().apply {
            addProperty("user_question", src.userQuestion)
            if (src.isSuggestedPrompt) addProperty("suggested_prompt", true)
        }
    }
}
