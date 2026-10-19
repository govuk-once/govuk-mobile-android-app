package uk.gov.govuk.govkit.date

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant

class InstantExtTest {

    // now = Friday 2026-08-28 13:00 BST (12:00 UTC)
    private val now = Instant.parse("2026-08-28T12:00:00Z")

    // --- toRelativeDate ---

    @Test
    fun `toRelativeDate, today, returns Today with time`() {
        val date = Instant.parse("2026-08-28T09:00:00Z") // 10:00am BST
        assertEquals("Today, 10:00am", date.toRelativeDate(now))
    }

    @Test
    fun `toRelativeDate, yesterday, returns Yesterday`() {
        val date = Instant.parse("2026-08-27T09:00:00Z")
        assertEquals("Yesterday", date.toRelativeDate(now))
    }

    @Test
    fun `toRelativeDate, within last 7 days, returns day name`() {
        val date = Instant.parse("2026-08-25T09:00:00Z") // Tuesday
        assertEquals("Tuesday", date.toRelativeDate(now))
    }

    @Test
    fun `toRelativeDate, exactly 7 days ago, returns formatted date`() {
        val date = Instant.parse("2026-08-21T09:00:00Z") // boundary: daysAgo == 7
        assertEquals("21 August", date.toRelativeDate(now))
    }

    @Test
    fun `toRelativeDate, older, returns formatted date`() {
        val date = Instant.parse("2026-07-29T09:00:00Z")
        assertEquals("29 July", date.toRelativeDate(now))
    }

    // --- toRelativeDateTime ---

    @Test
    fun `toRelativeDateTime, today, returns Today with time`() {
        val date = Instant.parse("2026-08-28T09:00:00Z") // 10:00am BST
        assertEquals("Today, 10:00am", date.toRelativeDateTime(now))
    }

    @Test
    fun `toRelativeDateTime, yesterday, returns Yesterday with time`() {
        val date = Instant.parse("2026-08-27T09:00:00Z") // 10:00am BST
        assertEquals("Yesterday, 10:00am", date.toRelativeDateTime(now))
    }

    @Test
    fun `toRelativeDateTime, older, returns formatted date with time`() {
        val date = Instant.parse("2026-07-29T09:00:00Z") // 10:00am BST
        assertEquals("29 July, 10:00am", date.toRelativeDateTime(now))
    }
}
