package uk.gov.govuk.notifications

import android.content.Context
import com.onesignal.OneSignal
import com.onesignal.notifications.INotificationClickEvent
import com.onesignal.notifications.INotificationClickListener
import uk.gov.govuk.notifications.navigation.DeepLinkLauncher
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OneSignalClient @Inject constructor(
    override val context: Context,
    private val launcher: DeepLinkLauncher
) : NotificationsProvider {

    override fun initialise(appId: String) {
        OneSignal.consentRequired = true
        OneSignal.initWithContext(context, appId)
    }

    override fun login(notificationId: String) {
        OneSignal.login(notificationId)
    }

    override fun logout() {
        OneSignal.logout()
    }

    override fun giveConsent() {
        OneSignal.consentGiven = true
    }

    override fun removeConsent() {
        OneSignal.consentGiven = false
    }

    override fun consentGiven() = OneSignal.consentGiven

    override suspend fun requestPermission() {
        val consentGiven = OneSignal.Notifications.requestPermission(false)
        OneSignal.consentGiven = consentGiven
    }

    override fun addClickListener() {
        OneSignal.Notifications.addClickListener(object : INotificationClickListener {
            override fun onClick(event: INotificationClickEvent) {
                val uri = event.notification.additionalData?.optString(NotificationSchema.KEY_DEEP_LINK)
                if (!uri.isNullOrEmpty()) {
                    launcher.launchDeepLink(uri)
                }
            }
        })
    }
}
