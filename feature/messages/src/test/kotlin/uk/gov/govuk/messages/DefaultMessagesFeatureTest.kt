package uk.gov.govuk.messages

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.messages.data.MessagesRepo
import uk.gov.govuk.messages.data.model.Notification
import uk.gov.govuk.messages.data.model.UpdateNotificationRequestBody
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DefaultMessagesFeatureTest {

    private val messagesRepo = mockk<MessagesRepo>(relaxed = true)
    private lateinit var feature: DefaultMessagesFeature

    @Before
    fun setup() {
        feature = DefaultMessagesFeature(messagesRepo)
    }

    @Test
    fun `getUnreadCount returns count of unread messages`() = runTest {
        val metadata = Notification.Metadata(Notification.Metadata.Sender("test"))
        coEvery { messagesRepo.getMessages() } returns Result.Success(
            listOf(
                Notification("1", "T", "B", "UNREAD", "2024-01-01T00:00:00Z", metadata = metadata),
                Notification("2", "T", "B", "READ",   "2024-01-01T00:00:00Z", metadata = metadata),
                Notification("3", "T", "B", "UNREAD", "2024-01-01T00:00:00Z", metadata = metadata),
            )
        )

        assertEquals(2, feature.getUnreadCount())
    }

    @Test
    fun `getUnreadCount returns null when repo returns an error`() = runTest {
        coEvery { messagesRepo.getMessages() } returns Result.Error()

        assertNull(feature.getUnreadCount())
    }

    @Test
    fun `markAsRead calls repo with READ status and the given id`() = runTest {
        feature.markAsRead("msg-123")

        coVerify(exactly = 1) {
            messagesRepo.updateMessage("msg-123", UpdateNotificationRequestBody.Status.READ)
        }
    }
}
