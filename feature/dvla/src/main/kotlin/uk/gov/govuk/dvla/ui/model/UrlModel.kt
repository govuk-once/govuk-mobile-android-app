package uk.gov.govuk.dvla.ui.model

internal data class UrlModel(
    val original: String,
    private val formatted: String? = null
) {
    val external = formatted ?: original
}
