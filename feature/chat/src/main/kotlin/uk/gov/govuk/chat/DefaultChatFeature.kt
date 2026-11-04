package uk.gov.govuk.chat

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import uk.gov.govuk.chat.data.ChatRepo

internal class DefaultChatFeature @Inject constructor(private val chatRepo: ChatRepo) : ChatFeature {

    override val shouldDisplayChatBanner: Flow<Boolean> = chatRepo.isChatIntroSeen.map { !it }

    override suspend fun clear() {
        chatRepo.clear()
    }
}
