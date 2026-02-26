package uk.govuk.app.local.domain

object PostcodeSanitizer {
    fun sanitize(postcode: String): String {
        return postcode.replace(Regex("\\W"), "")
            .replace("_", "")
            .uppercase()
    }
}
