package uk.gov.govuk.dvla.util

import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test

class StringProviderTest {

    private val stringProvider = mockk<StringProvider>()

    @Test
    fun `resolveSummaryDescription returns Unknown when resId is null`() {
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
}