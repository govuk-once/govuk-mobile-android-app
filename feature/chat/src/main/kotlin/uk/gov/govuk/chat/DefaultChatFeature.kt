package uk.gov.govuk.chat

import uk.gov.govuk.chat.data.ChatRepo
import javax.inject.Inject

internal class DefaultChatFeature @Inject constructor(
    private val chatRepo: ChatRepo
): ChatFeature {

    override suspend fun clear() {
        chatRepo.clear()
    }
}
