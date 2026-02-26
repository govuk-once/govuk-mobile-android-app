package uk.gov.govuk.config.extensions

import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test

class StringExtensionTest {

    private val lesserVersionNumbers = listOf(
        "1.0.9",
        "1",
        "1.0",
        "1.0.0",
        "0.0.100"
    )

    private val greaterVersionNumbers = listOf(
        "1.0.10",
        "2.0.0",
        "2.0",
        "2",
        "100.0.0"
    )

    @Test
    fun `Given the version number is less than the target version number, then the result is true`() {
        repeat(lesserVersionNumbers.size) {
            assertTrue(lesserVersionNumbers[it].isVersionLessThan(greaterVersionNumbers[it]))
        }
    }

    @Test
    fun `Given the version number is greater than the target version number, then the result is false`() {
        repeat(lesserVersionNumbers.size) {
            assertFalse(greaterVersionNumbers[it].isVersionLessThan(lesserVersionNumbers[it]))
        }
    }

    @Test
    fun `Given the version number is equal to the target version number, then the result is false`() {
        repeat(lesserVersionNumbers.size) {
            assertFalse(lesserVersionNumbers[it].isVersionLessThan(lesserVersionNumbers[it]))
        }
    }
}
