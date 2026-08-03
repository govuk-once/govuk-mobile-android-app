package uk.gov.govuk.dvla.util

import junit.framework.TestCase.assertEquals
import org.junit.Test

class SpecificationsFormatterTest {
    @Test
    fun `getFormattedEngineCapacity correctly formats when engine capacity is under 1000`() {
        val result = getFormattedEngineCapacity(999)
        assertEquals("999cc", result)
    }
}
