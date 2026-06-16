package uk.gov.govuk.dvla.util

import junit.framework.TestCase.assertEquals
import org.junit.Test

class SpecificationsFormatterTest {
    @Test
    fun `getFormattedEngineCapacity correctly formats when engine capacity is under 1000`() {
        val result = getFormattedEngineCapacity(999)
        assertEquals("999cc", result)
    }

    @Test
    fun `getFormattedEngineCapacity correctly formats when engine capacity is 1000 or over`() {
        val result = getFormattedEngineCapacity(1000)
        assertEquals("1.0L", result)
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

    @Test
    fun `getFormattedEmissionsAltText correctly formats`() {
        val result = getFormattedEmissionsAltText("200g/km", "grams per kilometre")
        assertEquals("200 grams per kilometre", result)
    }

    @Test
    fun `getFormattedVehicleColour correctly formats`() {
        val result = getFormattedVehicleColour("Red", "and", "Blue")
        assertEquals("Red and Blue", result)
    }
}
