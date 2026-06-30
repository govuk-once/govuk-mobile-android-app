package uk.gov.govuk.notificationcentre.fixtures

import uk.gov.govuk.notificationcentre.data.model.Notification
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class NotificationCentreFixtures {
    companion object {
        private val referenceDate: OffsetDateTime = LocalDateTime.now()
            .atOffset(ZoneOffset.ofHours(1))

        val mockNotifications = listOf(
            Notification(
                "1",
                "Title1",
                "Body1",
                "UNREAD",
                referenceDate.format(DateTimeFormatter.ISO_DATE_TIME)
            ),
            Notification(
                "2",
                "Title2",
                "Body2",
                "UNREAD",
                referenceDate.minusDays(21)
                    .format(DateTimeFormatter.ISO_DATE_TIME)
            )
        )
    }
}