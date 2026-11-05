package uk.gov.govuk.dvla.ui.model

internal data class UrlModel(
    val originalUrl: String,
    private val formattedUrl: String? = null
) {
    val urlToOpen = formattedUrl ?: originalUrl
}
