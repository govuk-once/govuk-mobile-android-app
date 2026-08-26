package uk.gov.govuk.messages.fixtures

import uk.gov.govuk.messages.data.model.Notification
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class MessagesFixtures {
    companion object {
        private val referenceDate: OffsetDateTime = LocalDateTime.now()
            .atOffset(ZoneOffset.ofHours(1))

        val metadata = Notification.Metadata(Notification.Metadata.Sender("test"))
        val mockMessages = listOf(
            Notification(
                "1",
                "Title1",
                "Body1",
                "UNREAD",
                referenceDate.format(DateTimeFormatter.ISO_DATE_TIME),
                metadata = metadata
            ),
            Notification(
                "2",
                "Title2",
                "Body2",
                "UNREAD",
                referenceDate.minusDays(3)
                    .format(DateTimeFormatter.ISO_DATE_TIME),
                metadata = metadata
            ),
            Notification(
                "2",
                "Title2",
                "Body2",
                "UNREAD",
                referenceDate.minusDays(21)
                    .format(DateTimeFormatter.ISO_DATE_TIME),
                metadata = metadata
            )
        )
    }
}