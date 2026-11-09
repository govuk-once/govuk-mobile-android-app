package uk.gov.govuk.analytics

import org.junit.Assert.assertEquals
import org.junit.Test
import uk.gov.govuk.analytics.extension.redactPii

class StringExtensionTest {

    companion object {
        private const val POSTCODE_REDACTION_TEXT = "[postcode]"
        private const val EMAIL_REDACTION_TEXT = "[email]"
        private const val NI_NUMBER_REDACTION_TEXT = "[NI number]"
    }

    @Test
    fun `Given a set of postcodes, when redacted, postcodes should be replaced`() {
        assertEquals(POSTCODE_REDACTION_TEXT, "L1 8JQ".redactPii())
        assertEquals(POSTCODE_REDACTION_TEXT, "W1A 0AX".redactPii())
        assertEquals(POSTCODE_REDACTION_TEXT, "L10 9HL".redactPii())
        assertEquals(POSTCODE_REDACTION_TEXT, "EH1 1YZ".redactPii())
        assertEquals(POSTCODE_REDACTION_TEXT, "CF10 1AA".redactPii())
        assertEquals(POSTCODE_REDACTION_TEXT, "EC1A 1BB".redactPii())

        assertEquals(POSTCODE_REDACTION_TEXT, "L18JQ".redactPii())
        assertEquals(POSTCODE_REDACTION_TEXT, "W1A0AX".redactPii())
        assertEquals(POSTCODE_REDACTION_TEXT, "L109HL".redactPii())
        assertEquals(POSTCODE_REDACTION_TEXT, "EH11YZ".redactPii())
        assertEquals(POSTCODE_REDACTION_TEXT, "CF101AA".redactPii())
        assertEquals(POSTCODE_REDACTION_TEXT, "EC1A1BB".redactPii())

        assertEquals(POSTCODE_REDACTION_TEXT, "L1  8JQ".redactPii())
        assertEquals(POSTCODE_REDACTION_TEXT, "W1A  0AX".redactPii())
        assertEquals(POSTCODE_REDACTION_TEXT, "L10  9HL".redactPii())
        assertEquals(POSTCODE_REDACTION_TEXT, "EH1  1YZ".redactPii())
        assertEquals(POSTCODE_REDACTION_TEXT, "CF10  1AA".redactPii())
        assertEquals(POSTCODE_REDACTION_TEXT, "EC1A  1BB".redactPii())

        val expected = "Universal credit $POSTCODE_REDACTION_TEXT allowance"

        assertEquals(expected, "Universal credit L1 8JQ allowance".redactPii())
        assertEquals(expected, "Universal credit W1A 0AX allowance".redactPii())
        assertEquals(expected, "Universal credit L10 9HL allowance".redactPii())
        assertEquals(expected, "Universal credit EH1 1YZ allowance".redactPii())
        assertEquals(expected, "Universal credit CF10 1AA allowance".redactPii())
        assertEquals(expected, "Universal credit EC1A 1BB allowance".redactPii())

        assertEquals(expected, "Universal credit L18JQ allowance".redactPii())
        assertEquals(expected, "Universal credit W1A0AX allowance".redactPii())
        assertEquals(expected, "Universal credit L109HL allowance".redactPii())
        assertEquals(expected, "Universal credit EH11YZ allowance".redactPii())
        assertEquals(expected, "Universal credit CF101AA allowance".redactPii())
        assertEquals(expected, "Universal credit EC1A1BB allowance".redactPii())

        assertEquals(expected, "Universal credit L1  8JQ allowance".redactPii())
        assertEquals(expected, "Universal credit W1A  0AX allowance".redactPii())
        assertEquals(expected, "Universal credit L10  9HL allowance".redactPii())
        assertEquals(expected, "Universal credit EH1  1YZ allowance".redactPii())
        assertEquals(expected, "Universal credit CF10  1AA allowance".redactPii())
        assertEquals(expected, "Universal credit EC1A  1BB allowance".redactPii())
    }

    @Test
    fun `Given a set of email addresses, when redacted, email addresses should be replaced`() {
        assertEquals(EMAIL_REDACTION_TEXT, "john.doe@example.com".redactPii())
        assertEquals(EMAIL_REDACTION_TEXT, "jane_doe123@company.co.uk".redactPii())
        assertEquals(EMAIL_REDACTION_TEXT, "user.name+tag@domain.org".redactPii())
        assertEquals(EMAIL_REDACTION_TEXT, "contact@sub.domain.net".redactPii())
        assertEquals(EMAIL_REDACTION_TEXT, "firstname.lastname@company.com".redactPii())
        assertEquals(EMAIL_REDACTION_TEXT, "user+mailbox@subdomain.example.com".redactPii())

        val expected = "Self assessment $EMAIL_REDACTION_TEXT tax return"

        assertEquals(expected, "Self assessment john.doe@example.com tax return".redactPii())
        assertEquals(expected, "Self assessment jane_doe123@company.co.uk tax return".redactPii())
        assertEquals(expected, "Self assessment user.name+tag@domain.org tax return".redactPii())
        assertEquals(expected, "Self assessment contact@sub.domain.net tax return".redactPii())
        assertEquals(expected, "Self assessment firstname.lastname@company.com tax return".redactPii())
        assertEquals(expected, "Self assessment user+mailbox@subdomain.example.com tax return".redactPii())
    }

    @Test
    fun `Given an NI number, when redacted, NI number should be replaced`() {
        assertEquals(NI_NUMBER_REDACTION_TEXT, "CD987654A".redactPii())
        assertEquals(NI_NUMBER_REDACTION_TEXT, "CD 98 76 54 A".redactPii())
        assertEquals(NI_NUMBER_REDACTION_TEXT, "CD  98  76  54  A".redactPii())

        val expected = "Child benefit $NI_NUMBER_REDACTION_TEXT allowance"
        assertEquals(expected, "Child benefit CD987654A allowance".redactPii())
        assertEquals(expected, "Child benefit CD 98 76 54 A allowance".redactPii())
        assertEquals(expected, "Child benefit CD  98  76  54  A allowance".redactPii())
    }

    @Test
    fun `Given all redaction types, when redacted, everything should be replaced`() {
        assertEquals(
            "$POSTCODE_REDACTION_TEXT $EMAIL_REDACTION_TEXT $NI_NUMBER_REDACTION_TEXT",
            "L9 8JQ test@email.com CD987654A".redactPii()
        )
    }
}