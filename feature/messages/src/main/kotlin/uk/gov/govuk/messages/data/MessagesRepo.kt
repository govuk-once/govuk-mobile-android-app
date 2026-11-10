package uk.gov.govuk.messages.data

import uk.gov.govuk.data.model.Result
import uk.gov.govuk.messages.data.model.Message
import uk.gov.govuk.messages.data.model.UpdateNotificationRequestBody
import java.time.Instant

interface DateProvider {
    val date: Instant
}

internal interface MessagesRepo {
    suspend fun getMessages(): Result<List<Message>>
    suspend fun getSingleMessage(messageId: String): Result<Message?>
    suspend fun updateMessage(messageId: String, status: UpdateNotificationRequestBody.Status): Result<Unit>
    suspend fun deleteMessage(messageId: String): Result<Unit>
}