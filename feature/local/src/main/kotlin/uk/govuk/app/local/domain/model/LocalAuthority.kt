package uk.govuk.app.local.domain.model

data class LocalAuthority(
    val name: String,
    val url: String,
    val slug: String,
    val parent: LocalAuthority? = null
)
