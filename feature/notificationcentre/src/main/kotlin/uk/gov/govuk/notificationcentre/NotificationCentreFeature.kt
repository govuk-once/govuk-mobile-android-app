package uk.gov.govuk.notificationcentre

fun interface NotificationCentreFeature {

    /**
     * Returns the number of unread notifications, or null if the count
     * could not be determined (e.g. an error or the device is offline).
     */
    suspend fun getUnreadCount(): Int?

}
