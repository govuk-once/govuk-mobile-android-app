package uk.gov.govuk.data.local

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RealmEncryptionHelper @Inject constructor(
    private val dataStore: RealmDataStore
) {

    private companion object {
        private const val KEYSTORE_KEY_ALIAS = "realm_key"
    }

    private val keyStore: KeyStore = KeyStore.getInstance("AndroidKeyStore")

    init {
        keyStore.load(null)
    }

    suspend fun getRealmKey(
        dispatcher: CoroutineDispatcher = Dispatchers.IO
    ): ByteArray {
        return withContext(dispatcher) {
            val encryptedRealmKey = dataStore.getRealmKey()
            val realmIv = dataStore.getRealmIv()

            if (encryptedRealmKey != null && realmIv != null &&
                keyStore.containsAlias(KEYSTORE_KEY_ALIAS)
            ) {
                decryptRealmKey(
                    encryptedKeyString = encryptedRealmKey,
                    ivString = realmIv
                ).encoded
            } else {
                val keystoreKey = createKeystoreKey()
                val realmEncryptionKey = createRealmEncryptionKey()
                encryptAndSaveRealmKey(realmEncryptionKey, keystoreKey)
                realmEncryptionKey
            }
        }
    }

    private fun decryptRealmKey(
        encryptedKeyString: String,
        ivString: String
    ): SecretKey {
        val encryptedKey = Base64.decode(encryptedKeyString, Base64.DEFAULT)
        val iv = Base64.decode(ivString, Base64.DEFAULT)

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, getKeystoreKey(), GCMParameterSpec(128, iv)) // Using the stored IV

        val decryptedKeyBytes = cipher.doFinal(encryptedKey)
        return SecretKeySpec(decryptedKeyBytes, "AES") // Convert to SecretKey
    }

    private fun getKeystoreKey(): SecretKey {
        val secretKeyEntry = keyStore.getEntry(KEYSTORE_KEY_ALIAS, null) as KeyStore.SecretKeyEntry
        return secretKeyEntry.secretKey
    }

    private fun createKeystoreKey(): SecretKey {
        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        val keyGenParameterSpec = KeyGenParameterSpec.Builder(
            KEYSTORE_KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256) // Generate a 256-bit key
            .build()

        keyGenerator.init(keyGenParameterSpec)
        return keyGenerator.generateKey()
    }

    private fun createRealmEncryptionKey(): ByteArray {
        val key = ByteArray(64) // 256 bits = 32 bytes
        val secureRandom = SecureRandom()
        secureRandom.nextBytes(key) // Fill the byte array with random bytes
        return key
    }

    private suspend fun encryptAndSaveRealmKey(realmKey: ByteArray, keystoreKey: SecretKey) {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, keystoreKey)

        val iv = cipher.iv
        val encryptedKey = cipher.doFinal(realmKey)

        val encodedKey = Base64.encodeToString(encryptedKey, Base64.DEFAULT)
        val encodedIv = Base64.encodeToString(iv, Base64.DEFAULT)

        dataStore.saveRealmKey(encodedKey)
        dataStore.saveRealmIv(encodedIv)
    }
}