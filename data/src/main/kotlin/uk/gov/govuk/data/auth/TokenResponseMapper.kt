package uk.gov.govuk.data.auth

import net.openid.appauth.TokenResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenResponseMapper @Inject constructor() {

    data class Tokens(
        val accessToken: String?,
        val idToken: String?,
        val refreshToken: String?
    )

    fun map(tokenResponse: TokenResponse?): Tokens {
        return Tokens(
            accessToken = tokenResponse?.accessToken,
            idToken = tokenResponse?.idToken,
            refreshToken = tokenResponse?.refreshToken
        )
    }
}