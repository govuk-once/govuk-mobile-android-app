package uk.gov.govuk.search.data.local

import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import uk.gov.govuk.data.local.RealmEncryptionHelper
import uk.gov.govuk.search.data.local.model.LocalSearchItem

internal fun interface RealmSearchReader {
    suspend fun read(block: suspend (List<LocalSearchItem>) -> Unit)
}

internal class DefaultRealmSearchReader(
    private val realmEncryptionHelper: RealmEncryptionHelper
) : RealmSearchReader {

    override suspend fun read(block: suspend (List<LocalSearchItem>) -> Unit) {
        val config = RealmConfiguration.Builder(schema = setOf(LocalSearchItem::class))
            .name("search")
            .schemaVersion(1)
            .encryptionKey(realmEncryptionHelper.getRealmKey())
            .build()
        val realm = Realm.open(config)
        try {
            val items = realm.query<LocalSearchItem>().find().toList()
            block(items)
        } finally {
            realm.close()
        }
        Realm.deleteRealm(config)
    }
}
