package uk.gov.govuk.config

import java.security.KeyFactory
import java.security.PublicKey
import java.security.Signature
import java.security.spec.X509EncodedKeySpec
import java.util.Base64
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SignatureValidator @Inject constructor() {
    private val key = BuildConfig.CONFIG_PUBLIC_KEY

    fun isValidSignature(
        signature: String,
        message: String
    ): Boolean {
        if (signature.isEmpty() || message.isEmpty()) {
            return false
        }

        val signatureBytes = Base64.getDecoder().decode(signature)
        val messageBytes = message.toByteArray()
        val publicKey = getPublicKey()

        val sig = Signature.getInstance("SHA256withECDSA")
        sig.initVerify(publicKey)
        sig.update(messageBytes)

        return sig.verify(signatureBytes)
    }

    private fun getPublicKey(): PublicKey {
        val keySpecPublic = X509EncodedKeySpec(Base64.getDecoder().decode(key))
        return KeyFactory.getInstance("EC").generatePublic(keySpecPublic)
    }
}
