package uk.gov.govuk.search.data.local

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.runBlocking
import uk.gov.govuk.analytics.AnalyticsClient

internal class SearchMigrationCallback(
    private val reader: RealmSearchReader,
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
                                    "INSERT OR REPLACE INTO local_search_items (searchTerm, timestamp) VALUES (?, ?)",
                                    arrayOf<Any?>(item.searchTerm, item.timestamp)
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
