package uk.gov.govuk.dwp.util

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class DateExtensionTest {

    @Test
    fun `toDistanceInWordsSummaryDisplayFormat correctly formats today's date`() {
        // 1 April 2026
        val date = LocalDate.of(2026, 4, 1)

        assertEquals("1 April 2026 (today)", date.toDistanceInWordsFormat(date))
    }

    @Test
    fun `toDistanceInWordsSummaryDisplayFormat correctly formats a day apart`() {
        // 1 April 2026
        val date = LocalDate.of(2026, 4, 1)

        val past = date.minusDays(1)
        val future = date.plusDays(1)

        assertEquals("31 March 2026 (yesterday)", date.toDistanceInWordsFormat(past))
        assertEquals("2 April 2026 (tomorrow)", date.toDistanceInWordsFormat(future))
    }

    @Test
    fun `toDistanceInWordsSummaryDisplayFormat correctly formats days apart`() {
        // 1 April 2026
        val date = LocalDate.of(2026, 4, 1)

        val past = date.minusDays(2)
        val future = date.plusDays(2)

        assertEquals("30 March 2026 (2 days ago)", date.toDistanceInWordsFormat(past))
        assertEquals("3 April 2026 (in 2 days)", date.toDistanceInWordsFormat(future))
    }

    @Test
    fun `toDistanceInWordsSummaryDisplayFormat correctly formats a week apart`() {
        // 1 April 2026
        val date = LocalDate.of(2026, 4, 1)

        val past = date.minusWeeks(1)
        val future = date.plusWeeks(1)

        assertEquals("25 March 2026 (last week)", date.toDistanceInWordsFormat(past))
        assertEquals("8 April 2026 (next week)", date.toDistanceInWordsFormat(future))
    }

    @Test
    fun `toDistanceInWordsSummaryDisplayFormat correctly formats weeks apart`() {
        // 1 April 2026
        val date = LocalDate.of(2026, 4, 1)

        val past = date.minusWeeks(2)
        val future = date.plusWeeks(2)

        assertEquals("18 March 2026 (2 weeks ago)", date.toDistanceInWordsFormat(past))
        assertEquals("15 April 2026 (in 2 weeks)", date.toDistanceInWordsFormat(future))
    }

    @Test
    fun `toDistanceInWordsSummaryDisplayFormat correctly formats a month apart`() {
        // 1 April 2026
        val date = LocalDate.of(2026, 4, 1)

        val past = date.minusMonths(1)
        val future = date.plusMonths(1)

        assertEquals("1 March 2026 (last month)", date.toDistanceInWordsFormat(past))
        assertEquals("1 May 2026 (next month)", date.toDistanceInWordsFormat(future))
    }

    @Test
    fun `toDistanceInWordsSummaryDisplayFormat correctly formats months apart`() {
        // 1 April 2026
        val date = LocalDate.of(2026, 4, 1)

        val past = date.minusMonths(2)
        val future = date.plusMonths(2)

        assertEquals("1 February 2026 (2 months ago)", date.toDistanceInWordsFormat(past))
        assertEquals("1 June 2026 (in 2 months)", date.toDistanceInWordsFormat(future))
    }

    @Test
    fun `toDistanceInWordsSummaryDisplayFormat correctly formats a year apart`() {
        // 1 April 2026
        val date = LocalDate.of(2026, 4, 1)

        val past = date.minusYears(1)
        val future = date.plusYears(1)

        assertEquals("1 April 2025 (last year)", date.toDistanceInWordsFormat(past))
        assertEquals("1 April 2027 (next year)", date.toDistanceInWordsFormat(future))
    }

    @Test
    fun `toDistanceInWordsSummaryDisplayFormat correctly formats years apart`() {
        // 1 April 2026
        val date = LocalDate.of(2026, 4, 1)

        val past = date.minusYears(2)
        val future = date.plusYears(2)

        assertEquals("1 April 2024 (2 years ago)", date.toDistanceInWordsFormat(past))
        assertEquals("1 April 2028 (in 2 years)", date.toDistanceInWordsFormat(future))
    }
}
