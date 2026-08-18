package uk.govuk.app.local.data.local

import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import uk.gov.govuk.data.local.RealmEncryptionHelper
import uk.govuk.app.local.data.local.model.StoredLocalAuthority
import uk.govuk.app.local.data.local.model.StoredLocalAuthorityParent

internal fun interface RealmLocalReader {
    suspend fun read(block: suspend (StoredLocalAuthority?) -> Unit)
}

internal class DefaultRealmLocalReader(
    private val realmEncryptionHelper: RealmEncryptionHelper
) : RealmLocalReader {

    override suspend fun read(block: suspend (StoredLocalAuthority?) -> Unit) {
        val config = RealmConfiguration.Builder(schema = setOf(StoredLocalAuthority::class, StoredLocalAuthorityParent::class))
            .name("local")
            .schemaVersion(1)
            .encryptionKey(realmEncryptionHelper.getRealmKey())
            .build()
        val realm = Realm.open(config)
        try {
            val item = realm.query<StoredLocalAuthority>().find().firstOrNull()
            block(item)
        } finally {
            realm.close()
        }
        Realm.deleteRealm(config)
    }
}
