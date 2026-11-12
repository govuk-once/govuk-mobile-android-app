package uk.gov.govuk.data.auth

import javax.inject.Inject
import javax.inject.Singleton
import net.openid.appauth.TokenResponse

@Singleton
class TokenResponseMapper @Inject constructor() {

    data class Tokens(
        val accessToken: String?,
        val idToken: String?,
        val refreshToken: String?
    )

    fun map(
        tokenResponse: TokenResponse?
    ): Tokens = Tokens(
        accessToken = tokenResponse?.accessToken,
        idToken = tokenResponse?.idToken,
        refreshToken = tokenResponse?.refreshToken
    )
}
