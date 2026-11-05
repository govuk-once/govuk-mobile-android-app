package uk.gov.govuk.extension

import android.net.Uri
import androidx.core.net.toUri

/**
 * Retrieves and validates a url query parameter from a Uri.
 *
 * Returns the url query parameter as a Uri if in allowed list else returns null.
 */
internal fun Uri.getUrlParam(allowedUrlParams: List<String>): Uri? {
    val urlParam = this.getQueryParameter("url")?.toUri() ?: return null
    if (urlParam.isInAllowedList(allowedUrlParams)) {
        return urlParam
    }
    return null
}

internal fun Uri.isInAllowedList(urls: List<String>): Boolean {
    val url = "${this.scheme}://${this.host}"
    return urls.contains(url)
}