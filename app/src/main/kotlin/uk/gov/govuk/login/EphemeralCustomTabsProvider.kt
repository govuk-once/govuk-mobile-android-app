package uk.gov.govuk.login

import android.content.Context
import android.content.Intent
import androidx.browser.customtabs.CustomTabsClient
import dagger.hilt.android.qualifiers.ApplicationContext
import uk.gov.govuk.data.auth.AuthRepo.Companion.AUTH_URL_KEY
import uk.gov.govuk.data.auth.AuthRepo.Companion.REDIRECT_URI_KEY
import uk.gov.govuk.data.auth.EphemeralBrowsingProvider
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EphemeralCustomTabsProvider @Inject constructor(
    @ApplicationContext private val context: Context
) : EphemeralBrowsingProvider {

    override val isSupported: Boolean
        get() = checkEphemeralBrowsingSupported()

    override fun fallbackIntent(authUrl: String, redirectUri: String): Intent {
        return Intent(context, SecureWebViewLoginActivity::class.java).apply {
            putExtra(AUTH_URL_KEY, authUrl)
            putExtra(REDIRECT_URI_KEY, redirectUri)
        }
    }

    private fun checkEphemeralBrowsingSupported(): Boolean {
        return try {
            val packageName = CustomTabsClient.getPackageName(context, null)
            packageName != null && CustomTabsClient.isEphemeralBrowsingSupported(context, packageName)
        } catch (_: Exception) {
            false
        }
    }
}
