package uk.gov.govuk.visited.data.store

import androidx.sqlite.db.SupportSQLiteDatabase
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.visited.data.model.VisitedItem

class VisitedMigrationCallbackTest {

    private val reader = mockk<RealmVisitedReader>()
    private val analyticsClient = mockk<AnalyticsClient>(relaxed = true)
    private val db = mockk<SupportSQLiteDatabase>(relaxed = true)
    private lateinit var callback: VisitedMigrationCallback

    @Before
    fun setup() {
        callback = VisitedMigrationCallback(reader, analyticsClient)
    }

    @Test
    fun `Given items in realm, when db opened, then insert items into room`() {
        val item = VisitedItem().apply { title = "GOV.UK"; url = "https://gov.uk"; lastVisited = 123L }
        coEvery { reader.read(any()) } coAnswers {
            firstArg<suspend (List<VisitedItem>) -> Unit>().invoke(listOf(item))
        }

        callback.onOpen(db)

        verify {
            db.execSQL(
                "INSERT OR REPLACE INTO visited_items (title, url, lastVisited) VALUES (?, ?, ?)",
                match { it.contentEquals(arrayOf<Any?>("GOV.UK", "https://gov.uk", 123L)) }
            )
        }
        verify { db.setTransactionSuccessful() }
    }

    @Test
    fun `Given no items in realm, when db opened, then no transaction started`() {
        coEvery { reader.read(any()) } coAnswers {
            firstArg<suspend (List<VisitedItem>) -> Unit>().invoke(emptyList())
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
