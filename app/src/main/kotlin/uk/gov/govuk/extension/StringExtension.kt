package uk.gov.govuk.extension

import androidx.navigation.NavDeepLink
import androidx.navigation.navDeepLink

/**
 * Formats a deep link path into a uri pattern for each url.
 *
 * Returns a collection of NavDeepLink objects for each formatted uri pattern.
 */
internal fun String.asDeepLinks(urls: List<String>): List<NavDeepLink> {
    val path = this
    val navDeepLinks = mutableListOf<NavDeepLink>()
    urls.forEach {
        navDeepLinks.add(navDeepLink {
            uriPattern = "$it$path"
        })
    }
    return navDeepLinks
}
