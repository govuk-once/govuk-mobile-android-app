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

    @Test
    fun `toYearDisplayFormat correctly formats local date`() {
        // 1 February 2027
        val date = LocalDate.of(2027, 2, 1)
        assertEquals("2027", date.toYearDisplayFormat())

        // 15 December 2024
        val date2 = LocalDate.of(2024, 12, 15)
        assertEquals("2024", date2.toYearDisplayFormat())
    }

    @Test
    fun `getNumberOfDaysWithinDayRangeAsPercentage correctly returns percentage`() {
        val todayPlusTwoDays = LocalDate.now().plusDays(2)
        val result = todayPlusTwoDays.getNumberOfDaysWithinDayRangeAsPercentage(4)
        assertEquals(50, result)
    }

    @Test
    fun `Given getNumberOfDaysFromNow is called, when date is 2 days ahead, then return 2`() {
        val todayPlusTwoDays = LocalDate.now().plusDays(2)
        assertEquals(2, todayPlusTwoDays.getNumberOfDaysFromNow())
    }

    @Test
    fun `Given isDateWithinDayRange is called, when date is 2 days ahead and threshold is 2 days, then return true`() {
        val todayPlusTwoDays = LocalDate.now().plusDays(2)
        assertEquals(true, todayPlusTwoDays.isDateWithinDayRange(2))
    }

    @Test
    fun `Given isDateWithinDayRange is called, when date is 4 days ahead and threshold is 2 days, then return false`() {
        val todayPlusTwoDays = LocalDate.now().plusDays(4)
        assertEquals(false, todayPlusTwoDays.isDateWithinDayRange(2))
    }

    @Test
    fun `Given isToday is called, when date is today, then return true`() {
        val todayPlusTwoDays = LocalDate.now()
        assertEquals(true, todayPlusTwoDays.isToday())
    }

    @Test
    fun `Given isToday is called, when date is tomorrow, then return false`() {
        val todayPlusTwoDays = LocalDate.now().plusDays(1)
        assertEquals(false, todayPlusTwoDays.isToday())
    }
}
