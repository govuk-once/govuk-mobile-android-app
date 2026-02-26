package uk.gov.govuk.ui

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsIntent.ACTIVITY_HEIGHT_FIXED
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.core.net.toUri

@Composable
internal fun rememberBrowserLauncher(shouldShowExternalBrowser: Boolean): BrowserActivityLauncher {
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {}
    return remember(launcher) {
        if (shouldShowExternalBrowser) {
            BrowserActivityLauncher.External(launcher)
        } else {
            BrowserActivityLauncher.InApp(launcher)
        }
    }
}

internal sealed class BrowserActivityLauncher(
    val launcher: ManagedActivityResultLauncher<Intent, ActivityResult>
) {
    abstract fun launch(url: String, onError: () -> Unit)

    open fun launchPartial(context: Context, url: String, onError: () -> Unit) {
        launch(url, onError)
    }

    internal class External(
        launcher: ManagedActivityResultLauncher<Intent, ActivityResult>
    ) : BrowserActivityLauncher(launcher) {
        override fun launch(url: String, onError: () -> Unit) {
            try {
                Intent(Intent.ACTION_VIEW).run {
                    data = url.toUri()
                    setFlags(FLAG_ACTIVITY_NEW_TASK)
                    launcher.launch(this)
                }
            } catch (e: ActivityNotFoundException) {
                onError()
            }
        }
    }

    internal class InApp(
        launcher: ManagedActivityResultLauncher<Intent, ActivityResult>
    ) : BrowserActivityLauncher(launcher) {
        override fun launch(url: String, onError: () -> Unit) {
            try {
                CustomTabsIntent.Builder().build().run {
                    intent.data = url.toUri()
                    intent.setFlags(FLAG_ACTIVITY_NEW_TASK)
                    launcher.launch(this.intent)
                }
            } catch (e: ActivityNotFoundException) {
                onError()
            }
        }

        override fun launchPartial(context: Context, url: String, onError: () -> Unit) {
            try {
                context.getPartialCustomTabsIntent().run {
                    intent.data = url.toUri()
                    launcher.launch(this.intent)
                }
            } catch (e: ActivityNotFoundException) {
                onError()
            }
        }
    }
}

private fun Context.getPartialCustomTabsIntent(): CustomTabsIntent {
    val displayMetrics = this.resources.displayMetrics
    val screenHeight = displayMetrics.heightPixels
    return CustomTabsIntent.Builder()
        .setInitialActivityHeightPx(
            screenHeight,
            ACTIVITY_HEIGHT_FIXED
        )
        .setBackgroundInteractionEnabled(false)
        .build()
}
