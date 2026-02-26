package uk.govuk.app.local.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class PostcodeSanitizerTest {
    @Test
    fun `Empty postcode`() {
        assertEquals("", PostcodeSanitizer.sanitize(""))
    }

    @Test
    fun `Postcode with only whitespace`() {
        assertEquals("", PostcodeSanitizer.sanitize("  "))
    }

    @Test
    fun `Postcode with leading whitespace`() {
        assertEquals("SW1A1AA", PostcodeSanitizer.sanitize(" SW1A1AA"))
    }

    @Test
    fun `Postcode with trailing whitespace`() {
        assertEquals("SW1A1AA", PostcodeSanitizer.sanitize("SW1A1AA "))
    }

    @Test
    fun `Postcode with mixed case letters`() {
        assertEquals("SW1A1AA", PostcodeSanitizer.sanitize(" Sw1A1aA"))
    }

    @Test
    fun `Postcode with combination of whitespace, special characters, letters and numbers`() {
        assertEquals("SW1A1AA", PostcodeSanitizer.sanitize("sw!@£\$%^&*_+-=1a[]{};:|,.//<>?`~1 (())a\\ \' a \" \""))
    }

    @Test
    fun `Postcode with only special characters`() {
        assertEquals("", PostcodeSanitizer.sanitize("!@£\$%^&*_+-=[]{};:|,.//<>?`~ (())\\ \'  \" \""))
    }

    @Test
    fun `Postcode with underscores`() {
        assertEquals("SW1A1AA", PostcodeSanitizer.sanitize("SW_1A_1A_A"))
    }

    @Test
    fun `Postcode with only underscores`() {
        assertEquals("", PostcodeSanitizer.sanitize("___"))
    }
}
