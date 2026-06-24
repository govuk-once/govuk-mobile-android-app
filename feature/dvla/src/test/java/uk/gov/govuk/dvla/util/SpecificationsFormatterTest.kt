package uk.gov.govuk.dvla.util

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import org.junit.Test

class SpecificationsFormatterTest {
    @Test
    fun `getFormattedEngineCapacity correctly formats when engine capacity is under 1000`() {
        val result = getFormattedEngineCapacity(999)
        assertEquals("999cc", result)
    }

    @Test
    fun `getFormattedEngineCapacity correctly formats when engine capacity is 1000`() {
        val result = getFormattedEngineCapacity(1000)
        assertEquals("1.0L", result)
    }

    @Test
    fun `getFormattedEngineCapacity correctly formats when engine capacity is 1950`() {
        val result = getFormattedEngineCapacity(1950)
        assertEquals("2.0L", result)
    }

    @Test
    fun `getFormattedEngineCapacity correctly formats when engine capacity is 1949`() {
        val result = getFormattedEngineCapacity(1949)
        assertEquals("1.9L", result)
    }

    @Test
    fun `getFormattedEngineCapacityAltText correctly formats when engine capacity doesn't have L`() {
        val result = getFormattedEngineCapacityAltText("999cc", "Litres")
        assertEquals("999cc", result)
    }

    @Test
    fun `getFormattedEngineCapacityAltText correctly formats when engine capacity has L`() {
        val result = getFormattedEngineCapacityAltText("1000L", "Litres")
        assertEquals("1000 Litres", result)
    }
}
