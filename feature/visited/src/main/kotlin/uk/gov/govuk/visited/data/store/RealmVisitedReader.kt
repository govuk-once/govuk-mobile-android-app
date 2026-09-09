package uk.gov.govuk.visited.data.store

import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import uk.gov.govuk.data.local.RealmEncryptionHelper
import uk.gov.govuk.visited.data.model.VisitedItem

internal fun interface RealmVisitedReader {
    suspend fun read(block: suspend (List<VisitedItem>) -> Unit)
}

internal class DefaultRealmVisitedReader(
    private val realmEncryptionHelper: RealmEncryptionHelper
) : RealmVisitedReader {

    override suspend fun read(block: suspend (List<VisitedItem>) -> Unit) {
        val config = RealmConfiguration.Builder(schema = setOf(VisitedItem::class))
            .name("visited")
            .schemaVersion(1)
            .encryptionKey(realmEncryptionHelper.getRealmKey())
            .build()
        val realm = Realm.open(config)
        try {
            val items = realm.query<VisitedItem>().find().toList()
            block(items)
        } finally {
            realm.close()
        }
        Realm.deleteRealm(config)
    }
}
