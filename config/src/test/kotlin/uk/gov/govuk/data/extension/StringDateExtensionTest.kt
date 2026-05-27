package uk.gov.govuk.data.extension

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate

class StringDateExtensionTest {

    @Test
    fun `Given a valid date string, when toLocalDateOrNull is called, then return correct LocalDate`() {
        val input = "2025-12-01"
        val expected = LocalDate.of(2025, 12, 1)

        val result = input.toLocalDateOrNull()
        assertEquals(expected, result)
    }

    @Test
    fun `Given a null string, when toLocalDateOrNull is called, then return null`() {
        val input: String? = null

        val result = input.toLocalDateOrNull()
        assertNull(result)
    }

    @Test
    fun `Given an empty string, when toLocalDateOrNull is called, then return null`() {
        val input = ""

        val result = input.toLocalDateOrNull()
        assertNull(result)
    }

    @Test
    fun `Given a random string, when toLocalDateOrNull is called, then return null`() {
        val input = "random string"

        val result = input.toLocalDateOrNull()

        assertNull(result)
    }
}

