package uk.gov.govuk.notifications

import android.content.Context
import com.onesignal.OneSignal
import com.onesignal.notifications.INotificationClickEvent
import com.onesignal.notifications.INotificationClickListener
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
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

    override fun requestPermission(
        dispatcher: CoroutineDispatcher,
        onCompleted: (() -> Unit)?
    ) {
        CoroutineScope(dispatcher).launch {
            val consentGiven = OneSignal.Notifications.requestPermission(false)
            OneSignal.consentGiven = consentGiven
            onCompleted?.invoke()
        }
    }

    override fun addClickListener() {
        OneSignal.Notifications.addClickListener(object : INotificationClickListener {
            override fun onClick(event: INotificationClickEvent) {
                handleAdditionalData(event.notification.additionalData)
            }
        })
    }
}
