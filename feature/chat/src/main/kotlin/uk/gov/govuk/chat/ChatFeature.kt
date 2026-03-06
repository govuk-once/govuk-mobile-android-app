package uk.gov.govuk.chat

import kotlinx.coroutines.flow.Flow

interface ChatFeature {

    val shouldDisplayChatBanner: Flow<Boolean>

    suspend fun clear()
}
