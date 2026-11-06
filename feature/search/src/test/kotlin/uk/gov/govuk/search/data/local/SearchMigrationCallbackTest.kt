package uk.gov.govuk.search.data.local

import androidx.sqlite.db.SupportSQLiteDatabase
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.search.data.local.model.LocalSearchItem

class SearchMigrationCallbackTest {

    private val reader = mockk<RealmSearchReader>()
    private val analyticsClient = mockk<AnalyticsClient>(relaxed = true)
    private val db = mockk<SupportSQLiteDatabase>(relaxed = true)
    private lateinit var callback: SearchMigrationCallback

    @Before
    fun setup() {
        callback = SearchMigrationCallback(reader, analyticsClient)
    }

    @Test
    fun `Given items in realm, when db opened, then insert items into room`() {
        val item = LocalSearchItem().apply { searchTerm = "cat"; timestamp = 123L }
        coEvery { reader.read(any()) } coAnswers {
            firstArg<suspend (List<LocalSearchItem>) -> Unit>().invoke(listOf(item))
        }

        callback.onOpen(db)

        verify {
            db.execSQL(
                "INSERT OR REPLACE INTO local_search_items (searchTerm, timestamp) VALUES (?, ?)",
                match { it.contentEquals(arrayOf<Any?>("cat", 123L)) }
            )
        }
        verify { db.setTransactionSuccessful() }
    }

    @Test
    fun `Given no items in realm, when db opened, then no transaction started`() {
        coEvery { reader.read(any()) } coAnswers {
            firstArg<suspend (List<LocalSearchItem>) -> Unit>().invoke(emptyList())
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
