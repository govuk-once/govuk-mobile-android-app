package uk.gov.govuk.notifications.navigation

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeepLinkLauncher @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    fun launchDeepLink(uri: String) {
        if (uri.isBlank()) {
            return
        }
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName) ?: return
        intent.data = uri.toUri()
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
    }
}