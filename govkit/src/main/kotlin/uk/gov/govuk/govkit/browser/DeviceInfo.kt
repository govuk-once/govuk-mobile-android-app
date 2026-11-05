package uk.gov.govuk.govkit.browser

import android.os.Build

data class DeviceInfo(
    val manufacturer: String,
    val model: String,
    val osVersion: String
) {
    companion object {
        fun fromBuild() = DeviceInfo(
            manufacturer = Build.MANUFACTURER,
            model = Build.MODEL,
            osVersion = Build.VERSION.RELEASE
        )
    }
}
