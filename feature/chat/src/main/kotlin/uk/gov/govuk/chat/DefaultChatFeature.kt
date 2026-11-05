package uk.gov.govuk.chat

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import uk.gov.govuk.chat.data.ChatRepo
import javax.inject.Inject

internal class DefaultChatFeature @Inject constructor(
    private val chatRepo: ChatRepo
): ChatFeature {

    override val shouldDisplayChatBanner: Flow<Boolean> = chatRepo.isChatIntroSeen.map { !it }

    override suspend fun clear() {
        chatRepo.clear()
    }
}
