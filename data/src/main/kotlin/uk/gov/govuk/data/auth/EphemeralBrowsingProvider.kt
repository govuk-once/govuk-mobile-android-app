package uk.gov.govuk.data.auth

import android.content.Intent

interface EphemeralBrowsingProvider {
    val isSupported: Boolean
    fun fallbackIntent(authUrl: String, redirectUri: String): Intent
}
