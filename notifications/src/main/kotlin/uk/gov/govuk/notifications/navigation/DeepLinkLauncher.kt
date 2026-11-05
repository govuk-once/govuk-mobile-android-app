package uk.gov.govuk.notifications.navigation

import android.content.Context
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
        context.startActivity(intent)
    }
}
