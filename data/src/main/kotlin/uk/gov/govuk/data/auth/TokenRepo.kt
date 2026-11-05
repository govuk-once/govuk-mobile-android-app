package uk.gov.govuk.data.auth

import uk.gov.govuk.data.auth.local.TokenDataStore
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenRepo @Inject constructor(
    private val tokenDataStore: TokenDataStore
) {
    internal suspend fun getSubId() = tokenDataStore.getSubId()

    internal suspend fun saveSubId(subId: String) = tokenDataStore.saveSubId(subId)
}
