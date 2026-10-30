package uk.govuk.app.local.data.local

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.runBlocking
import uk.gov.govuk.analytics.AnalyticsClient

internal class LocalMigrationCallback(
    private val reader: RealmLocalReader,
    private val analyticsClient: AnalyticsClient
) : RoomDatabase.Callback() {

    override fun onOpen(db: SupportSQLiteDatabase) {
        try {
            runBlocking {
                reader.read { item ->
                    if (item != null) {
                        db.beginTransaction()
                        try {
                            db.execSQL(
                                "INSERT OR REPLACE INTO local_authority (id, name, url, slug, parent_name, parent_url, parent_slug) VALUES (1, ?, ?, ?, ?, ?, ?)",
                                arrayOf<Any?>(item.name, item.url, item.slug, item.parent?.name, item.parent?.url, item.parent?.slug)
                            )
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
