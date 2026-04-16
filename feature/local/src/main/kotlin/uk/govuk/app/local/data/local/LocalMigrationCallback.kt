package uk.govuk.app.local.data.local

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import kotlinx.coroutines.runBlocking
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.data.local.RealmEncryptionHelper
import uk.govuk.app.local.data.local.model.StoredLocalAuthority
import uk.govuk.app.local.data.local.model.StoredLocalAuthorityParent

internal class LocalMigrationCallback(
    private val realmEncryptionHelper: RealmEncryptionHelper,
    private val analyticsClient: AnalyticsClient
) : RoomDatabase.Callback() {

    override fun onOpen(db: SupportSQLiteDatabase) {
        val config = runBlocking {
            val key = realmEncryptionHelper.getRealmKey()
            RealmConfiguration.Builder(schema = setOf(StoredLocalAuthority::class, StoredLocalAuthorityParent::class))
                .name("local")
                .schemaVersion(1)
                .encryptionKey(key)
                .build()
        }

        val item = try {
            val realm = Realm.open(config)
            val result = realm.query<StoredLocalAuthority>().find().firstOrNull()?.let { stored ->
                listOf(stored.name, stored.url, stored.slug,
                    stored.parent?.name, stored.parent?.url, stored.parent?.slug)
            }
            realm.close()
            result
        } catch (e: Exception) {
            analyticsClient.logException(e)
            return
        }

        if (item != null) {
            db.beginTransaction()
            try {
                db.execSQL(
                    "INSERT OR REPLACE INTO local_authority (id, name, url, slug, parent_name, parent_url, parent_slug) VALUES (1, ?, ?, ?, ?, ?, ?)",
                    arrayOf(item[0], item[1], item[2], item[3], item[4], item[5])
                )
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
