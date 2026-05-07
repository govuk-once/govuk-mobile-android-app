package uk.gov.govuk.topics.data.local

import androidx.sqlite.db.SupportSQLiteDatabase
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.topics.data.local.model.LocalTopicItem

class TopicsMigrationCallbackTest {

    private val reader = mockk<RealmTopicsReader>()
    private val analyticsClient = mockk<AnalyticsClient>(relaxed = true)
    private val db = mockk<SupportSQLiteDatabase>(relaxed = true)
    private lateinit var callback: TopicsMigrationCallback

    @Before
    fun setup() {
        callback = TopicsMigrationCallback(reader, analyticsClient)
    }

    @Test
    fun `Given items in realm, when db opened, then insert items into room`() {
        val item = LocalTopicItem().apply { ref = "ref1"; title = "Title"; description = "Desc"; isSelected = true }
        coEvery { reader.read(any()) } coAnswers {
            firstArg<suspend (List<LocalTopicItem>) -> Unit>().invoke(listOf(item))
        }

        callback.onOpen(db)

        verify {
            db.execSQL(
                "INSERT OR REPLACE INTO topic_items (ref, title, description, isSelected) VALUES (?, ?, ?, ?)",
                match { it.contentEquals(arrayOf<Any?>("ref1", "Title", "Desc", 1)) }
            )
        }
        verify { db.setTransactionSuccessful() }
    }

    @Test
    fun `Given no items in realm, when db opened, then no transaction started`() {
        coEvery { reader.read(any()) } coAnswers {
            firstArg<suspend (List<LocalTopicItem>) -> Unit>().invoke(emptyList())
        }

        callback.onOpen(db)

        verify(exactly = 0) { db.beginTransaction() }
    }

    @Test
    fun `Given reader throws, when db opened, then log exception`() {
        val exception = RuntimeException("realm error")
        coEvery { reader.read(any()) } throws exception

        callback.onOpen(db)

        verify { analyticsClient.logException(exception) }
    }
}
