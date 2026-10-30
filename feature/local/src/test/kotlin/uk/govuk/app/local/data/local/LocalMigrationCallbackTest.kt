package uk.govuk.app.local.data.local

import androidx.sqlite.db.SupportSQLiteDatabase
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.analytics.AnalyticsClient
import uk.govuk.app.local.data.local.model.StoredLocalAuthority
import uk.govuk.app.local.data.local.model.StoredLocalAuthorityParent

class LocalMigrationCallbackTest {

    private val reader = mockk<RealmLocalReader>()
    private val analyticsClient = mockk<AnalyticsClient>(relaxed = true)
    private val db = mockk<SupportSQLiteDatabase>(relaxed = true)
    private lateinit var callback: LocalMigrationCallback

    @Before
    fun setup() {
        callback = LocalMigrationCallback(reader, analyticsClient)
    }

    @Test
    fun `Given authority without parent in realm, when db opened, then insert authority into room with null parent fields`() {
        val authority = StoredLocalAuthority().apply { name = "Test Council"; url = "https://example.com"; slug = "test-council" }
        coEvery { reader.read(any()) } coAnswers {
            firstArg<suspend (StoredLocalAuthority?) -> Unit>().invoke(authority)
        }

        callback.onOpen(db)

        verify {
            db.execSQL(
                "INSERT OR REPLACE INTO local_authority (id, name, url, slug, parent_name, parent_url, parent_slug) VALUES (1, ?, ?, ?, ?, ?, ?)",
                match { it.contentEquals(arrayOf<Any?>("Test Council", "https://example.com", "test-council", null, null, null)) }
            )
        }
        verify { db.setTransactionSuccessful() }
    }

    @Test
    fun `Given authority with parent in realm, when db opened, then insert authority and parent fields into room`() {
        val parent = StoredLocalAuthorityParent().apply { name = "Parent Council"; url = "https://parent.com"; slug = "parent-council" }
        val authority = StoredLocalAuthority().apply {
            name = "Child Council"; url = "https://child.com"; slug = "child-council"
            this.parent = parent
        }
        coEvery { reader.read(any()) } coAnswers {
            firstArg<suspend (StoredLocalAuthority?) -> Unit>().invoke(authority)
        }

        callback.onOpen(db)

        verify {
            db.execSQL(
                "INSERT OR REPLACE INTO local_authority (id, name, url, slug, parent_name, parent_url, parent_slug) VALUES (1, ?, ?, ?, ?, ?, ?)",
                match { it.contentEquals(arrayOf<Any?>("Child Council", "https://child.com", "child-council", "Parent Council", "https://parent.com", "parent-council")) }
            )
        }
        verify { db.setTransactionSuccessful() }
    }

    @Test
    fun `Given no authority in realm, when db opened, then no transaction started`() {
        coEvery { reader.read(any()) } coAnswers {
            firstArg<suspend (StoredLocalAuthority?) -> Unit>().invoke(null)
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
