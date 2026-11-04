package uk.gov.govuk.messages

import javax.inject.Inject
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.messages.data.MessagesRepo
import uk.gov.govuk.messages.data.model.UpdateNotificationRequestBody

internal class DefaultMessagesFeature @Inject constructor(private val messagesRepo: MessagesRepo) : MessagesFeature {

    override suspend fun getUnreadCount(): Int? = when (val result = messagesRepo.getMessages()) {
        is Result.Success -> result.value.count { it.isUnread }
        else -> null
    }

    override suspend fun markAsRead(messageId: String) {
        messagesRepo.updateMessage(messageId, UpdateNotificationRequestBody.Status.READ)
    }
}
