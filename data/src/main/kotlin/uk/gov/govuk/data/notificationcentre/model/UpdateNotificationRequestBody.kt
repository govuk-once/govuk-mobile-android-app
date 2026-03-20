package uk.gov.govuk.data.notificationcentre.model

import com.google.gson.annotations.SerializedName

data class UpdateNotificationRequestBody(@SerializedName("Status") val status: Status) {
    enum class Status {
        READ,
        @SerializedName("MARKED_AS_UNREAD")
        UNREAD
    }
}