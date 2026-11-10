package uk.gov.govuk.data.local

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import uk.gov.govuk.data.crypto.CryptoProvider
import java.security.SecureRandom
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomEncryptionHelper @Inject constructor(
    @ApplicationContext private val context: Context,
    private val cryptoProvider: CryptoProvider
) {

    private companion object {
        private const val PREFS_NAME = "room_keyset_prefs"
        private const val DB_KEY_PREF = "room_db_key"
        private const val KEY_SIZE_BYTES = 32
    }

    fun getKey(): ByteArray {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val stored = prefs.getString(DB_KEY_PREF, null)
        return if (stored != null) {
            cryptoProvider.decrypt(stored).getOrThrow()
        } else {
            val key = ByteArray(KEY_SIZE_BYTES).also { SecureRandom().nextBytes(it) }
            val encrypted = cryptoProvider.encrypt(key).getOrThrow()
            prefs.edit().putString(DB_KEY_PREF, encrypted).apply()
            key
        }
    }
}
