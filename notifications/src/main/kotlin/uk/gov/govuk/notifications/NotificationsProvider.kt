package uk.gov.govuk.notifications

import android.content.Context
import androidx.core.app.NotificationManagerCompat

interface NotificationsProvider {
    val context: Context

    fun initialise()
    fun login(
        pushId: String
    )
    fun logout()
    fun giveConsent()
    fun removeConsent()
    fun consentGiven(): Boolean
    fun permissionGranted() = NotificationManagerCompat.from(context).areNotificationsEnabled()
    suspend fun requestPermission(): Boolean
    fun addClickListener()

    /**
     * What the OneSignal SDK has saved for this device. Only read it when the user has
     * consented. Throws if the SDK failed to initialise.
     */
    fun sdkState(): NotificationsSdkState
}

data class NotificationsSdkState(
    val externalId: String,
    val onesignalId: String,
    val pushSubscriptionId: String,
    val pushOptedIn: Boolean
)
