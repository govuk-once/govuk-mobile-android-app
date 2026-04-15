package uk.gov.govuk.data.local

import android.content.Context
import android.util.Base64
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeyTemplates
import com.google.crypto.tink.RegistryConfiguration
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.security.SecureRandom
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomEncryptionHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private companion object {
        private const val PREFS_NAME = "room_keyset_prefs"
        private const val KEYSET_NAME = "room_keyset"
        private const val MASTER_KEY_URI = "android-keystore://room_master_key"
        private const val DB_KEY_PREF = "room_db_key"
        private const val KEY_SIZE_BYTES = 32
    }

    fun getKey(): ByteArray {
        AeadConfig.register()
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        val aead = AndroidKeysetManager.Builder()
            .withSharedPref(context, KEYSET_NAME, PREFS_NAME)
            .withKeyTemplate(KeyTemplates.get("AES256_GCM"))
            .withMasterKeyUri(MASTER_KEY_URI)
            .build()
            .keysetHandle
            .getPrimitive(RegistryConfiguration.get(), Aead::class.java)

        val stored = prefs.getString(DB_KEY_PREF, null)
        return if (stored != null) {
            aead.decrypt(Base64.decode(stored, Base64.DEFAULT), null)
        } else {
            val key = ByteArray(KEY_SIZE_BYTES).also { SecureRandom().nextBytes(it) }
            val encrypted = aead.encrypt(key, null)
            prefs.edit().putString(DB_KEY_PREF, Base64.encodeToString(encrypted, Base64.DEFAULT)).apply()
            key
        }
    }
}
