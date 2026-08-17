package uk.gov.govuk.dvla.ui.model

import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Test

class UrlModelTest {
    @Test
    fun `Given a url model has an original and formatted url, then the fields are correct`() = runTest {
        val result = UrlModel("https://www.original.com", "https://www.formatted.com")
        assertEquals("https://www.original.com", result.originalUrl)
        assertEquals("https://www.formatted.com", result.urlToOpen)
    }

    @Test
    fun `Given a url model has an original url, then the fields are correct`() = runTest {
        val result = UrlModel("https://www.original.com")
        assertEquals("https://www.original.com", result.originalUrl)
        assertEquals("https://www.original.com", result.urlToOpen)
    }
}
