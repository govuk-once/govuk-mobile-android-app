package uk.gov.govuk.extension

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class StringExtensionTest {
    @Test
    fun `Given a deep link path, When asDeepLinks() called on it with a list of allowed urls, should return a collection of deep links`() {
        val allowedUrls = listOf("scheme://host")

        runTest {
            val deepLinks = "/path".asDeepLinks(allowedUrls)
            assertEquals(1, deepLinks.size)
            assertEquals("scheme://host/path", deepLinks[0].uriPattern)
        }
    }

    @Test
    fun `Given a deep link path, When asDeepLinks() called on it with an empty list, should return an empty collection of deep links`() {
        val allowedUrls = listOf<String>()

        runTest {
            val deepLinks = "/path".asDeepLinks(allowedUrls)
            assertTrue(deepLinks.isEmpty())
        }
    }
}
