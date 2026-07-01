package uk.gov.govuk.dvla.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class StringExtensionTest {

    @Test
    fun `toTitleCase formats strings correctly`() {
        assertEquals("Milton Keynes", "MILTON KEYNES".toTitleCase())
        assertEquals("Anna Ornella Arenö", "anna ornella arenö".toTitleCase())

        assertEquals("", "".toTitleCase())
        assertEquals("", "   ".toTitleCase())
    }

    @Test
    fun `toSpacedString formats strings for TalkBack correctly`() {
        assertEquals("F H 0 8 P D H", "FH08PDH".toSpacedString())
        assertEquals("1 2 3", "123".toSpacedString())

        assertEquals("", "".toSpacedString())
        assertEquals(" ", " ".toSpacedString())
    }

    @Test
    fun `Given isDirectDebit is called, when the string is Direct Debit, then return true`() {
        val result = "Direct Debit".isDirectDebit()
        assertTrue(result)
    }

    @Test
    fun `Given isDirectDebit is called, when the string is Not Direct Debit, then return false`() {
        val result = "Not Direct Debit".isDirectDebit()
        assertFalse(result)
    }
}