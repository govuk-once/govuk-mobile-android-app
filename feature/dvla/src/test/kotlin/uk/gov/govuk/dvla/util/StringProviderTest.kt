package uk.gov.govuk.dvla.util

import io.mockk.every
import io.mockk.mockk
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test
import uk.gov.govuk.dvla.R

class StringProviderTest {

    private val stringProvider = mockk<StringProvider>()

    private companion object {
        const val TODAY_RES = 1
        const val DAYS_RES = 2
    }

    @Test
    fun `resolveSummaryDescription returns Unknown when resId is null`() {
        every { stringProvider.getString(R.string.status_unknown) } returns "Unknown"

        val result = stringProvider.resolveSummaryDescription(resId = null, dateArg = "1 January 2030")
        assertEquals("Unknown", result)
    }

    @Test
    fun `resolveSummaryDescription returns formatted string when date is provided`() {
        val resId = 123
        every { stringProvider.getString(resId, "1 January 2030") } returns "Valid until 1 January 2030"

        val result = stringProvider.resolveSummaryDescription(resId = resId, dateArg = "1 January 2030")
        assertEquals("Valid until 1 January 2030", result)
    }

    @Test
    fun `resolveSummaryDescription returns standard string when date is null`() {
        val resId = 123
        every { stringProvider.getString(resId, "") } returns "Expired"

        val result = stringProvider.resolveSummaryDescription(resId = resId, dateArg = null)
        assertEquals("Expired", result)
    }

    @Test
    fun `Given today, when resolveDaysUntil called, then the today string is returned`() {
        every { stringProvider.getString(TODAY_RES) } returns "Today"

        assertEquals("Today", stringProvider.resolveDaysUntil(LocalDate.now(), TODAY_RES, DAYS_RES))
    }

    @Test
    fun `Given tomorrow, when resolveDaysUntil called, then the plural is resolved with 1`() {
        every { stringProvider.getQuantityString(DAYS_RES, 1, 1) } returns "1 day"

        assertEquals("1 day", stringProvider.resolveDaysUntil(LocalDate.now().plusDays(1), TODAY_RES, DAYS_RES))
    }

    @Test
    fun `Given a future date, when resolveDaysUntil called, then the plural is resolved with the day count`() {
        every { stringProvider.getQuantityString(DAYS_RES, 10, 10) } returns "10 days"

        assertEquals("10 days", stringProvider.resolveDaysUntil(LocalDate.now().plusDays(10), TODAY_RES, DAYS_RES))
    }
}
