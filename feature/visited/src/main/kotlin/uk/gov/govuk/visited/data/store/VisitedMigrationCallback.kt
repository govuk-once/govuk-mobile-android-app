package uk.gov.govuk.visited.data.store

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import kotlinx.coroutines.runBlocking
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.data.local.RealmEncryptionHelper
import uk.gov.govuk.visited.data.model.VisitedItem

internal class VisitedMigrationCallback(
    private val realmEncryptionHelper: RealmEncryptionHelper,
    private val analyticsClient: AnalyticsClient
) : RoomDatabase.Callback() {

    override fun onOpen(db: SupportSQLiteDatabase) {
        val config = runBlocking {
            val key = realmEncryptionHelper.getRealmKey()
            RealmConfiguration.Builder(schema = setOf(VisitedItem::class))
                .name("visited")
                .schemaVersion(1)
                .encryptionKey(key)
                .build()
        }

        try {
            val realm = Realm.open(config)
            try {
                val items = realm.query<VisitedItem>().find()
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
            } finally {
                realm.close()
            }
            Realm.deleteRealm(config)
        } catch (e: Exception) {
            analyticsClient.logException(e)
        }
    }
}
