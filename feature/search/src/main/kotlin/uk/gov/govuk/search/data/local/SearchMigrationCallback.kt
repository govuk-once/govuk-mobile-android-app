package uk.gov.govuk.search.data.local

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import kotlinx.coroutines.runBlocking
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.data.local.RealmEncryptionHelper
import uk.gov.govuk.search.data.local.model.LocalSearchItem

internal class SearchMigrationCallback(
    private val realmEncryptionHelper: RealmEncryptionHelper,
    private val analyticsClient: AnalyticsClient
) : RoomDatabase.Callback() {

    override fun onOpen(db: SupportSQLiteDatabase) {
        val config = runBlocking {
            val key = realmEncryptionHelper.getRealmKey()
            RealmConfiguration.Builder(schema = setOf(LocalSearchItem::class))
                .name("search")
                .schemaVersion(1)
                .encryptionKey(key)
                .build()
        }

        val items = try {
            val realm = Realm.open(config)
            val result = realm.query<LocalSearchItem>().find()
                .map { Pair(it.searchTerm, it.timestamp) }
            realm.close()
            result
        } catch (e: Exception) {
            analyticsClient.logException(e)
            return
        }

        if (items.isNotEmpty()) {
            db.beginTransaction()
            try {
                items.forEach { (searchTerm, timestamp) ->
                    db.execSQL(
                        "INSERT OR REPLACE INTO local_search_items (searchTerm, timestamp) VALUES (?, ?)",
                        arrayOf(searchTerm, timestamp)
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
