package uk.gov.govuk.dvla.data

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class DeviceIdProvider @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // for POC, remove when linkingId for DVLA is available, suppress warning
    @SuppressLint("HardwareIds")
    fun getDeviceId(): String =
        Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
            ?: "fallback_id_${System.currentTimeMillis()}"

}