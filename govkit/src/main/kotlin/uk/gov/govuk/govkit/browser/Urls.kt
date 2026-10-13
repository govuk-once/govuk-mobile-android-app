package uk.gov.govuk.govkit.browser

import java.net.URLEncoder

object Urls {

    const val PLAY_STORE = "https://play.google.com/store/apps/details?id=uk.gov.govuk"
    const val GOV_UK_HOME = "https://www.gov.uk"
    const val HELP_AND_FEEDBACK_URL = "https://www.gov.uk/contact/govuk-app"
}

fun getHelpAndFeedbackUrl(appVersion: String, deviceInfo: DeviceInfo): String {
    val deviceString = "${deviceInfo.manufacturer} ${deviceInfo.model} ${deviceInfo.osVersion}"
    return "${Urls.HELP_AND_FEEDBACK_URL}?" +
            "app_version=$appVersion&" +
            "phone=${URLEncoder.encode(deviceString, "UTF-8")}"
}