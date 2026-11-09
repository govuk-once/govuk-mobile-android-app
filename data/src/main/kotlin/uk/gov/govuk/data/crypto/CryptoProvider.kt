package uk.gov.govuk.data.crypto

interface CryptoProvider {
    fun encrypt(input: ByteArray): Result<String>

    fun decrypt(inputEncrypted: String): Result<ByteArray>
}