package uk.gov.govuk.data.local

import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.migration.AutomaticSchemaMigration
import io.realm.kotlin.types.TypedRealmObject
import kotlin.reflect.KClass

abstract class RealmProvider(
    private val encryptionHelper: RealmEncryptionHelper
) {

    private lateinit var realm: Realm

    abstract val schemaVersion: Long
    abstract val name: String
    abstract val schema: Set<KClass<out TypedRealmObject>>
    open val migration: AutomaticSchemaMigration? = null

    suspend fun open(): Realm {
        if (!::realm.isInitialized) {
            val realmKey = encryptionHelper.getRealmKey()

            val config = RealmConfiguration.Builder(schema = schema)
                .schemaVersion(schemaVersion)
                .name(name)
                .encryptionKey(realmKey)

            migration?.let {
                config.migration(it)
            }

            realm = Realm.open(config.build())
        }

        return realm
    }
}