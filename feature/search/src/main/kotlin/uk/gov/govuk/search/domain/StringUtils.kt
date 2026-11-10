package uk.gov.govuk.search.domain

object StringUtils {
    fun buildFullUrl(relativePath: String): String {
        return when {
            relativePath.startsWith("http") -> relativePath
            else -> "${SearchConfig.BASE_URL}$relativePath"
        }
    }

    fun collapseWhitespace(string: String): String {
        return string.replace("""\s+""".toRegex(), " ").trim()
    }
}
