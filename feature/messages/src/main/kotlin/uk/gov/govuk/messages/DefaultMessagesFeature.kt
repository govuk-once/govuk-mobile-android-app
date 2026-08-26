package uk.gov.govuk.messages

import uk.gov.govuk.data.model.Result
import uk.gov.govuk.messages.data.MessagesRepo
import javax.inject.Inject

internal class DefaultMessagesFeature @Inject constructor(
    private val messagesRepo: MessagesRepo
): MessagesFeature {

    override suspend fun getUnreadCount(): Int? {
        return when (val result = messagesRepo.getMessages()) {
            is Result.Success -> result.value.count { it.isUnread }
            else -> null
        }
    }

}
