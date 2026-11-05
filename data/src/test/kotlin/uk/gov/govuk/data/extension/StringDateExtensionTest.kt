package uk.gov.govuk.data.extension

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate

class StringDateExtensionTest {
    @Test
    fun `Given a valid date string, then return the correct LocalDate`() {
        val dateString = "2024-01-01"
        val expected = LocalDate.of(2024, 1, 1)

        val result = dateString.toLocalDateOrNull()

        assertEquals(expected, result)
    }

    @Test
    fun `Given a null string, then return null`() {
        val dateString = null

        assertNull(dateString.toLocalDateOrNull())
    }

    @Test
    fun `Given an empty string, then return null`() {
        val dateString = ""

        assertNull(dateString.toLocalDateOrNull())
    }

    @Test
    fun `Given an invalid date string, then return null`() {
        val dateString = "01-01-2024"

        assertNull(dateString.toLocalDateOrNull())
    }

    @Test
    fun `Given a non-date string, then return null`() {
        val dateString = "not-a-date"

        assertNull(dateString.toLocalDateOrNull())
    }
}
