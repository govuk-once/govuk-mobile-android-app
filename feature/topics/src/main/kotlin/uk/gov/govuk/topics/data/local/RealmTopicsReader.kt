package uk.gov.govuk.topics.data.local

import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import uk.gov.govuk.data.local.RealmEncryptionHelper
import uk.gov.govuk.topics.data.local.model.LocalTopicItem

internal fun interface RealmTopicsReader {
    suspend fun read(block: suspend (List<LocalTopicItem>) -> Unit)
}

internal class DefaultRealmTopicsReader(
    private val realmEncryptionHelper: RealmEncryptionHelper
) : RealmTopicsReader {

    override suspend fun read(block: suspend (List<LocalTopicItem>) -> Unit) {
        val config = RealmConfiguration.Builder(schema = setOf(LocalTopicItem::class))
            .name("topics")
            .schemaVersion(1)
            .encryptionKey(realmEncryptionHelper.getRealmKey())
            .build()
        val realm = Realm.open(config)
        try {
            val items = realm.query<LocalTopicItem>().find().toList()
            block(items)
        } finally {
            realm.close()
        }
        Realm.deleteRealm(config)
    }
}
