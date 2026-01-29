package uk.gov.govuk.notifications

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.json.JSONObject

interface NotificationsProvider {
    val context: Context

    companion object {
        private const val DEEP_LINK = "deeplink"
    }

    fun initialise(appId: String)
    fun giveConsent()
    fun removeConsent()
    fun consentGiven(): Boolean
    fun permissionGranted() =
        NotificationManagerCompat.from(context).areNotificationsEnabled()
    fun requestPermission(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        onCompleted: (() -> Unit)? = null
    )
    fun addClickListener()
    fun handleAdditionalData(
        additionalData: JSONObject?,
        intent: Intent? = context.packageManager.getLaunchIntentForPackage(context.packageName)
    ) {
        additionalData ?: return
        intent ?: return
        if (additionalData.has(DEEP_LINK)) {
            val deepLink = additionalData.optString(DEEP_LINK)
            intent.data = deepLink.toUri()
            intent.flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
        }
    }
}
