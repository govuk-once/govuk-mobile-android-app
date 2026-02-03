package uk.gov.govuk.notifications

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import androidx.core.net.toUri
import com.onesignal.OneSignal
import com.onesignal.notifications.INotificationClickEvent
import com.onesignal.notifications.INotificationClickListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OneSignalClient @Inject constructor(
    override val context: Context
) : NotificationsProvider {

    override fun initialise(appId: String) {
        OneSignal.consentRequired = true
        OneSignal.initWithContext(context, appId)
    }

    override fun giveConsent() {
        OneSignal.consentGiven = true
    }

    override fun removeConsent() {
        OneSignal.consentGiven = false
    }

    override fun consentGiven() = OneSignal.consentGiven

    override suspend fun requestPermission() {
        withContext(Dispatchers.IO) {
            val consentGiven = OneSignal.Notifications.requestPermission(false)
            OneSignal.consentGiven = consentGiven
        }
    }

    override fun addClickListener() {
        OneSignal.Notifications.addClickListener(object : INotificationClickListener {
            override fun onClick(event: INotificationClickEvent) {
                handleAdditionalData(event.notification.additionalData)
            }
        })
    }

    override fun handleAdditionalData(
        additionalData: JSONObject?,
        intent: Intent?
    ) {
        additionalData ?: return
        intent ?: return
        if (additionalData.has("deeplink")) {
            val deepLink = additionalData.optString("deeplink")
            intent.data = deepLink.toUri()
            intent.flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
        }
    }
}
