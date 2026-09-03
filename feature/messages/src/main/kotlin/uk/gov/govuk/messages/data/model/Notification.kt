package uk.gov.govuk.messages.data.model

import com.google.gson.annotations.SerializedName
import java.time.Instant

data class MessageGroups(val recent: List<Message>, val older: List<Message>)

typealias Message = Notification

data class Notification(
    @SerializedName("NotificationID")
    val id: String,
    @SerializedName("NotificationTitle")
    val title: String,
    @SerializedName("NotificationBody")
    val body: String,
    @SerializedName("Status")
    val status: String,
    @SerializedName("DispatchedDateTime")
    val rawDate: String,
    @SerializedName("MessageTitle")
    val messageTitle: String? = null,
    @SerializedName("MessageBody")
    val messageBody: String? = null,
    @SerializedName("Metadata")
    val metadata: Metadata
) {

    data class Metadata(
        @SerializedName("Sender")
        val sender: Sender) {
        data class Sender(
            @SerializedName("DisplayName")
            val displayName: String)
    }


    val date: Instant
        get() = Instant.parse(rawDate)

    val isUnread: Boolean
        get() = status != "READ"


}