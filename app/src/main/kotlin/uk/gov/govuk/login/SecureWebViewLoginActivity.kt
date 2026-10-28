package uk.gov.govuk.login

import android.content.Intent
import android.os.Bundle
import android.webkit.CookieManager
import android.webkit.ServiceWorkerController
import android.webkit.WebSettings.LOAD_NO_CACHE
import android.webkit.WebSettings.MIXED_CONTENT_NEVER_ALLOW
import android.webkit.WebStorage
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.core.net.toUri
import uk.gov.govuk.data.auth.AuthRepo.Companion.AUTH_URL_KEY
import uk.gov.govuk.data.auth.AuthRepo.Companion.CODE_KEY
import uk.gov.govuk.data.auth.AuthRepo.Companion.ERROR_KEY
import uk.gov.govuk.data.auth.AuthRepo.Companion.REDIRECT_URI_KEY
import uk.gov.govuk.data.auth.AuthRepo.Companion.REDIRECT_URL_KEY
import uk.gov.govuk.data.auth.AuthRepo.Companion.STATE_KEY

class SecureWebViewLoginActivity : ComponentActivity() {

    private lateinit var webView: WebView
    private var authUrl: String? = null
    private var redirectUri: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authUrl = intent.getStringExtra(AUTH_URL_KEY)
        redirectUri = intent.getStringExtra(REDIRECT_URI_KEY)

        if (authUrl.isNullOrEmpty() || redirectUri.isNullOrEmpty()) {
            setResult(RESULT_CANCELED)
            finish()
            return
        }

        webView = WebView(this)
        setContentView(webView)

        configureWebViewForEphemeralBrowsing()
        authUrl?.let { webView.loadUrl(it) }
    }

    private fun configureWebViewForEphemeralBrowsing() {
        webView.apply {
            settings.apply {
                cacheMode = LOAD_NO_CACHE
                domStorageEnabled = false
                mixedContentMode = MIXED_CONTENT_NEVER_ALLOW
            }

            clearAllWebData()
            blockServiceWorkers()
            webViewClient = OAuthRedirectWebViewClient()
        }
    }

    override fun onDestroy() {
        clearAllWebData()
        super.onDestroy()
    }

    private fun clearAllWebData() {
        // Clear cookies, cache, history and stored file data

        CookieManager.getInstance().removeAllCookies(null)
        CookieManager.getInstance().flush()

        with(webView){
            clearCache(true)
            clearHistory()
            clearFormData()
            clearSslPreferences()
            clearMatches()
        }
        WebStorage.getInstance().deleteAllData()
    }

    private fun blockServiceWorkers() {
        ServiceWorkerController.getInstance()
            .serviceWorkerWebSettings.blockNetworkLoads = true
    }

    private inner class OAuthRedirectWebViewClient : WebViewClient() {
        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            view.evaluateJavascript(
                "Object.defineProperty(navigator, 'locks', { value: null });",
                null
            )
        }

        @Deprecated("Deprecated in Java")
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            redirectUri?.let {
                if (url.startsWith(it)) {
                    handleRedirectUri(url)
                    return true
                }
            }
            return false
        }

        private fun handleRedirectUri(redirectUrl: String) {
            try {
                val uri = redirectUrl.toUri()
                val code = uri.getQueryParameter(CODE_KEY)
                val state = uri.getQueryParameter(STATE_KEY)
                val error = uri.getQueryParameter(ERROR_KEY)

                if (!code.isNullOrEmpty() && !state.isNullOrEmpty()) {
                    CookieManager.getInstance().removeAllCookies(null)
                    setResult(RESULT_OK, Intent().putExtra(REDIRECT_URL_KEY, redirectUrl))
                    finish()
                } else if (!error.isNullOrEmpty()) {
                    CookieManager.getInstance().removeAllCookies(null)
                    setResult(RESULT_CANCELED)
                    finish()
                }
            } catch (_: Exception) {
                CookieManager.getInstance().removeAllCookies(null)
                setResult(RESULT_CANCELED)
                finish()
            }
        }
    }
}
