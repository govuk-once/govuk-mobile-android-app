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

        val items = try {
            val realm = Realm.open(config)
            val result = realm.query<VisitedItem>().find()
                .map { Triple(it.title, it.url, it.lastVisited) }
            realm.close()
            result
        } catch (e: Exception) {
            analyticsClient.logException(e)
            return
        }

        if (items.isNotEmpty()) {
            db.beginTransaction()
            try {
                items.forEach { (title, url, lastVisited) ->
                    db.execSQL(
                        "INSERT OR REPLACE INTO visited_items (title, url, lastVisited) VALUES (?, ?, ?)",
                        arrayOf(title, url, lastVisited)
                    )
                }
                db.setTransactionSuccessful()
            } finally {
                db.endTransaction()
            }
        }

        try {
            Realm.deleteRealm(config)
        } catch (e: Exception) {
            analyticsClient.logException(e)
        }
    }
}
