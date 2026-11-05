package uk.gov.govuk.data.auth.model

data class Tokens(
    val accessToken: String = "",
    val idToken: String = "",
    val refreshToken: String = ""
)
