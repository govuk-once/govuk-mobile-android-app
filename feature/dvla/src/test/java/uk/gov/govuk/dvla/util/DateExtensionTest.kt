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
        assertEquals(50f, result)
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
        val todayPlusFourDays = LocalDate.now().plusDays(4)
        assertEquals(false, todayPlusFourDays.isDateWithinDayRange(2))
    }

    @Test
    fun `Given isToday is called, when date is today, then return true`() {
        val today = LocalDate.now()
        assertEquals(true, today.isToday())
    }

    @Test
    fun `Given isToday is called, when date is tomorrow, then return false`() {
        val todayPlusOneDay = LocalDate.now().plusDays(1)
        assertEquals(false, todayPlusOneDay.isToday())
    }

    @Test
    fun `Given isInThePast is called, when date is today, then return false`() {
        val today = LocalDate.now()
        assertEquals(false, today.isInThePast())
    }

    @Test
    fun `Given isInThePast is called, when date is yesterday, then return true`() {
        val todayMinusOneDay = LocalDate.now().minusDays(1)
        assertEquals(true, todayMinusOneDay.isInThePast())
    }
}
