package uk.gov.govuk.login

import android.content.Intent
import android.os.Bundle
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.core.net.toUri
import android.webkit.WebSettings.LOAD_NO_CACHE
import android.webkit.WebSettings.MIXED_CONTENT_NEVER_ALLOW

class SecureWebViewLoginActivity : ComponentActivity() {

    private lateinit var webView: WebView
    private var authUrl: String = ""
    private var redirectUri: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authUrl = intent.getStringExtra("auth_url") ?: ""
        redirectUri = intent.getStringExtra("redirect_uri") ?: ""

        webView = WebView(this)
        setContentView(webView)

        configureWebViewForEphemeralBrowsing()
        webView.loadUrl(authUrl)
    }

    private fun configureWebViewForEphemeralBrowsing() {
        webView.apply {
            settings.apply {
                cacheMode = LOAD_NO_CACHE
                domStorageEnabled = false
                mixedContentMode = MIXED_CONTENT_NEVER_ALLOW
            }

            CookieManager.getInstance().apply {
                setAcceptCookie(true)
                removeAllCookies(null)
            }

            webViewClient = OAuthRedirectWebViewClient()
        }
    }

    override fun onDestroy() {
        CookieManager.getInstance().removeAllCookies(null)
        super.onDestroy()
    }

    private inner class OAuthRedirectWebViewClient : WebViewClient() {

        @Deprecated("Deprecated in Java")
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            if (url.startsWith(redirectUri)) {
                handleRedirectUri(url)
                return true
            }
            return false
        }

        private fun handleRedirectUri(redirectUrl: String) {
            try {
                val uri = redirectUrl.toUri()
                val code = uri.getQueryParameter("code")
                val state = uri.getQueryParameter("state")
                val error = uri.getQueryParameter("error")

                if (!code.isNullOrEmpty() && !state.isNullOrEmpty()) {
                    CookieManager.getInstance().removeAllCookies(null)
                    setResult(RESULT_OK, Intent().putExtra("redirect_url", redirectUrl))
                    finish()
                } else if (!error.isNullOrEmpty()) {
                    CookieManager.getInstance().removeAllCookies(null)
                    setResult(RESULT_CANCELED)
                    finish()
                }
            } catch (e: Exception) {
                CookieManager.getInstance().removeAllCookies(null)
                setResult(RESULT_CANCELED)
                finish()
            }
        }
    }
}
