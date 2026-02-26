package uk.gov.govuk.config

import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test

class SignatureValidatorTest {
    val validSignature =
        "MEQCIGarfsSjptu9emvMEaugzf2qX9egR0IUfHtNw2VgdxxNAiAQMHUxDR4Z2ZsgEFV9eM6KzTgEEefDcAfBSERqDKmWhg=="

    val validMessage = "{\n" +
        "  \"platform\": \"Android\",\n" +
        "  \"config\": {\n" +
        "    \"available\": true,\n" +
        "    \"minimumVersion\": \"0.0.1\",\n" +
        "    \"recommendedVersion\": \"0.0.1\",\n" +
        "    \"releaseFlags\": {\n" +
        "      \"onboarding\": true,\n" +
        "      \"search\": true,\n" +
        "      \"topics\": true\n" +
        "    },\n" +
        "    \"version\": \"0.0.2\",\n" +
        "    \"lastUpdated\": \"2024-10-03T08:54:10.150Z\"\n" +
        "  },\n" +
        "  \"signature\": \"MEYCIQD9yINgfaeOijbYQ+LaZcwtMYfDULu6h5wsvyo5T9JLVAIhAJtrg815DuktCHJlil2ny0dmKqZgRHAhUo4CTc0Xrtzi\"\n" +
        "}"

    val invalidMessage = "{\n" +
        "  \"platform\": \"I've been tampered with\",\n" +
        "  \"config\": {\n" +
        "    \"available\": true,\n" +
        "    \"minimumVersion\": \"0.0.1\",\n" +
        "    \"recommendedVersion\": \"0.0.1\",\n" +
        "    \"releaseFlags\": {\n" +
        "      \"onboarding\": true,\n" +
        "      \"search\": true,\n" +
        "      \"topics\": true\n" +
        "    },\n" +
        "    \"version\": \"0.0.2\",\n" +
        "    \"lastUpdated\": \"2024-10-03T08:54:10.150Z\"\n" +
        "  },\n" +
        "  \"signature\": \"MEYCIQD9yINgfaeOijbYQ+LaZcwtMYfDULu6h5wsvyo5T9JLVAIhAJtrg815DuktCHJlil2ny0dmKqZgRHAhUo4CTc0Xrtzi\"\n" +
        "}"

    @Test
    fun `Given an empty string signature, then the validation is false`() {
        assertFalse(SignatureValidator().isValidSignature("", validMessage))
    }

    @Test
    fun `Given an empty string message, then the validation is false`() {
        assertFalse(SignatureValidator().isValidSignature(validSignature, ""))
    }

    @Test
    fun `Given a valid key, signature and message, then the validation is true`() {
        assertTrue(SignatureValidator().isValidSignature(validSignature, validMessage))
    }

    @Test
    fun `Given a valid key and signature, and an invalid message, then the validation is false`() {
        assertFalse(SignatureValidator().isValidSignature(validSignature, invalidMessage))
    }
}
