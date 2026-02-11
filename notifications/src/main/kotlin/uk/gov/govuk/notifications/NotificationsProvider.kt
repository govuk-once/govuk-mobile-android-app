package uk.gov.govuk.notifications

import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import org.json.JSONObject

interface NotificationsProvider {
    val context: Context

    fun initialise(appId: String)
    fun giveConsent()
    fun removeConsent()
    fun consentGiven(): Boolean
    fun permissionGranted() =
        NotificationManagerCompat.from(context).areNotificationsEnabled()
    suspend fun requestPermission()
    fun addClickListener()
}
