package uk.gov.govuk.visited.data

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDateTime
import java.time.ZoneOffset

class DateFormatterTest {
    @Test
    fun `Given a date in epoch days, then you get back a formatted date string`() {
        val dayInMillis = LocalDateTime.of(2023, 10, 15, 0, 0, 0).toEpochSecond(ZoneOffset.UTC)

        val actual = localDateFormatter(dayInMillis)
        val expected = "15 October"

        assertEquals(expected, actual)
    }

    @Test
    fun `Given a date in epoch days, then you get back a formatted date string - with the day minus leading zero`() {
        val dayInMillis = LocalDateTime.of(2023, 10, 1, 0, 0, 0).toEpochSecond(ZoneOffset.UTC)

        val actual = localDateFormatter(dayInMillis)
        val expected = "1 October"

        assertEquals(expected, actual)
    }

    @Test
    fun `Given an uppercase month, then you get back a capitalised month`() {
        val month = "OCTOBER"

        val actual = capitaliseMonth(month)
        val expected = "October"

        assertEquals(expected, actual)
    }

    @Test
    fun `Given a lower month, then you get back a capitalised month`() {
        val month = "october"

        val actual = capitaliseMonth(month)
        val expected = "October"

        assertEquals(expected, actual)
    }

    @Test
    fun `Given a capitalised month, then you get back a capitalised month`() {
        val month = "October"

        val actual = capitaliseMonth(month)
        val expected = "October"

        assertEquals(expected, actual)
    }
}
