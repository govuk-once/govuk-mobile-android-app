package uk.gov.govuk.notificationcentre.data.model

import com.google.gson.annotations.SerializedName
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale

data class NotificationGroups(val recent: List<Notification>, val older: List<Notification>)


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

    /**
        Formats a date to match the rules for Message List

        Today -> Today, 9:45pm

        Yesterday -> Yesterday

        Last 7 days -> Tuesday

        Else -> 7 December
     */
    val formattedDate: String
        get() {

            val london = ZoneId.of("Europe/London")
            val zonedSelf = date.atZone(london)
            val today = Instant.now().atZone(london)

            val isToday = zonedSelf.toLocalDate() == today.toLocalDate()
            val isYesterday = zonedSelf.toLocalDate() == today.toLocalDate().minusDays(1)
            val daysAgo = ChronoUnit.DAYS.between(zonedSelf.toLocalDate(), today.toLocalDate())

            return when {
                isToday -> {
                    val formatter = DateTimeFormatter.ofPattern("'Today,' h:mma", Locale.getDefault())
                        .withZone(london)
                    formatter.format(date)
                        .replace("AM", "am")
                        .replace("PM", "pm")
                }
                isYesterday -> "Yesterday"
                daysAgo < 7 -> {
                    zonedSelf.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
                }
                else -> {
                    val formatter = DateTimeFormatter.ofPattern("d MMMM", Locale.getDefault())
                        .withZone(london)
                    formatter.format(date)
                }
            }
        }

    val detailFormattedDate: String
        get() {
            val london = ZoneId.of("Europe/London")
            val zonedSelf = date.atZone(london)
            val today = Instant.now().atZone(london)

            val isToday = zonedSelf.toLocalDate() == today.toLocalDate()
            val isYesterday = zonedSelf.toLocalDate() == today.toLocalDate().minusDays(1)

            val pattern = when {
                isToday -> "'Today,' h:mma"
                isYesterday -> "'Yesterday,' h:mma"
                else -> "d MMMM, h:mma"
            }

            val formatter = DateTimeFormatter.ofPattern(pattern, Locale.getDefault())
                .withZone(london)

            return formatter.format(date)
                .replace("AM", "am")
                .replace("PM", "pm")
        }

}