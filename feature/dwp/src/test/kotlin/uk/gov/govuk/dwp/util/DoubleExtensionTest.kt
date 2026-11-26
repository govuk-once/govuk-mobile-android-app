package uk.gov.govuk.dwp.util

import org.junit.Assert.assertEquals
import org.junit.Test

class DoubleExtensionTest {
    @Test
    fun `convert formats whole number with two commas and two decimal places`() {
        assertEquals("1,000,000.00", 1000000.0.convert())
    }

    @Test
    fun `convert formats whole number with two decimal places`() {
        assertEquals("1,000.00", 1000.0.convert())
    }

    @Test
    fun `convert formats decimal value with comma grouping`() {
        assertEquals("1,234.56", 1234.56.convert())
    }

    @Test
    fun `convert formats decimal value with comma grouping when 01`() {
        assertEquals("1,234.01", 1234.01.convert())
    }

    @Test
    fun `convert formats decimal value with comma grouping when 10`() {
        assertEquals("1,234.10", 1234.10.convert())
    }

    @Test
    fun `convert formats value under 1000 without comma`() {
        assertEquals("999.99", 999.99.convert())
    }

    @Test
    fun `convert formats zero`() {
        assertEquals("0.00", 0.0.convert())
    }
}
