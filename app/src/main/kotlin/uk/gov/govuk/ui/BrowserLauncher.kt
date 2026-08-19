package uk.gov.govuk.ui

import android.app.PendingIntent
import android.content.ActivityNotFoundException
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsIntent.ACTIVITY_HEIGHT_FIXED
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import uk.gov.govuk.R

@Composable
internal fun rememberBrowserLauncher(shouldShowExternalBrowser: Boolean): BrowserActivityLauncher {
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {}

    val context = LocalContext.current

    return remember(launcher, context) {
        if (shouldShowExternalBrowser) {
            BrowserActivityLauncher.External(launcher)
        } else {
            BrowserActivityLauncher.InApp(launcher, context)
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
                    launcher.launch(this)
                }
            } catch (_: ActivityNotFoundException) {
                onError()
            }
        }
    }

    internal class InApp(
        launcher: ManagedActivityResultLauncher<Intent, ActivityResult>,
        private val context: Context
    ) : BrowserActivityLauncher(launcher) {
        override fun launch(url: String, onError: () -> Unit) {
            try {
                val builder = CustomTabsIntent.Builder()
                context.addCalendarButtonToCct(builder)

                builder.build().run {
                    intent.data = url.toUri()
                    launcher.launch(this.intent)
                }
            } catch (_: ActivityNotFoundException) {
                onError()
            }
        }

        override fun launchPartial(context: Context, url: String, onError: () -> Unit) {
            try {
                context.getPartialCustomTabsIntent().run {
                    intent.data = url.toUri()
                    launcher.launch(this.intent)
                }
            } catch (_: ActivityNotFoundException) {
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

private fun Context.addCalendarButtonToCct(builder: CustomTabsIntent.Builder) {
    val pendingIntent = PendingIntent.getBroadcast(
        this,
        0,
        Intent(this, AddToCalendarReceiver::class.java),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
    )

    ContextCompat.getDrawable(this, R.drawable.ic_calendar_edit_24)?.toBitmap()?.let { icon ->
        builder.setActionButton(icon, "Add to Calendar", pendingIntent)
    }

    builder.setShareState(CustomTabsIntent.SHARE_STATE_ON)
}

internal class AddToCalendarReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val currentUrl = intent.dataString ?: return

        val calendarIntent = Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.Events.DESCRIPTION, "Saved from: $currentUrl")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        context.startActivity(calendarIntent)
    }
}
