package uk.gov.govuk.login.data

import javax.inject.Inject
import javax.inject.Singleton
import uk.gov.govuk.login.data.local.LoginDataStore

@Singleton
class LoginRepo @Inject constructor(private val loginDataStore: LoginDataStore) {
    internal suspend fun getRefreshTokenExpiryDate() = loginDataStore.getRefreshTokenExpiryDate()

    internal suspend fun getRefreshTokenIssuedAtDate() = loginDataStore.getRefreshTokenIssuedAtDate()

    internal suspend fun setRefreshTokenIssuedAtDate(
        issuedAtDate: Long
    ) {
        loginDataStore.setRefreshTokenIssuedAtDate(issuedAtDate)
    }

    internal suspend fun clear() = loginDataStore.clear()
}
