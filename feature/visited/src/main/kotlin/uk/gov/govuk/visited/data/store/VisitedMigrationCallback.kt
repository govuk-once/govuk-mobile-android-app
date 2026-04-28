package uk.gov.govuk.visited.data.store

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.runBlocking
import uk.gov.govuk.analytics.AnalyticsClient

internal class VisitedMigrationCallback(
    private val reader: RealmVisitedReader,
    private val analyticsClient: AnalyticsClient
) : RoomDatabase.Callback() {

    override fun onOpen(db: SupportSQLiteDatabase) {
        try {
            runBlocking {
                reader.read { items ->
                    if (items.isNotEmpty()) {
                        db.beginTransaction()
                        try {
                            items.forEach { item ->
                                db.execSQL(
                                    "INSERT OR REPLACE INTO visited_items (title, url, lastVisited) VALUES (?, ?, ?)",
                                    arrayOf<Any?>(item.title, item.url, item.lastVisited)
                                )
                            }
                            db.setTransactionSuccessful()
                        } finally {
                            db.endTransaction()
                        }
                    }
                }
            }
        } catch (e: Exception) {
            analyticsClient.logException(e)
        }
    }
}
