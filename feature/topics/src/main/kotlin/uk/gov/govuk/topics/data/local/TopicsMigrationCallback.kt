package uk.gov.govuk.topics.data.local

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.runBlocking
import uk.gov.govuk.analytics.AnalyticsClient

internal class TopicsMigrationCallback(
    private val reader: RealmTopicsReader,
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
                                    "INSERT OR REPLACE INTO topic_items (ref, title, description, isSelected) VALUES (?, ?, ?, ?)",
                                    arrayOf<Any?>(item.ref, item.title, item.description, if (item.isSelected) 1 else 0)
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
