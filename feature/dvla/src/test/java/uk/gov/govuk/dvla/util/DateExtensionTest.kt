package uk.gov.govuk.dvla.util

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class DateExtensionTest {

    @Test
    fun `toSummaryDisplayFormat correctly formats local date`() {
        // 1 February 2027
        val date = LocalDate.of(2027, 2, 1)
        assertEquals("1 February 2027", date.toSummaryDisplayFormat())

        // 15 December 2024
        val date2 = LocalDate.of(2024, 12, 15)
        assertEquals("15 December 2024", date2.toSummaryDisplayFormat())
    }
}