package uk.gov.govuk.visited.data

import android.content.Context
import android.content.Intent
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.net.toUri

@Singleton
internal class VisitedShortcutPublisher @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    fun pushShortcut(title: String, url: String) {
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)?.apply {
            action = Intent.ACTION_VIEW
            data = url.toUri()
        } ?: return

        val shortcut = ShortcutInfoCompat.Builder(context, url)
            .setShortLabel(title.take(15))
            .setLongLabel(title.take(30))
            .setIcon(IconCompat.createWithResource(context, uk.gov.govuk.design.R.drawable.ic_external_link))
            .setIntent(intent)
            .build()

        ShortcutManagerCompat.pushDynamicShortcut(context, shortcut)
    }

    fun removeShortcut(url: String) {
        ShortcutManagerCompat.removeDynamicShortcuts(context, listOf(url))
    }

    fun clearAll() {
        ShortcutManagerCompat.removeAllDynamicShortcuts(context)
    }
}