package uk.gov.govuk.dvla.ui.model

data class UrlModel(
    private val original: String,
    private val formatted: String? = null
) {
    val external = formatted ?: original
    val internal = original
}
