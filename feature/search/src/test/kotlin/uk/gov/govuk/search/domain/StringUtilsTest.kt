package uk.gov.govuk.search.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class StringUtilsTest {
    @Test
    fun `buildFullUrl returns a full url when given a path`() {
        val url = StringUtils.buildFullUrl("/hello-world")

        assertEquals("https://www.gov.uk/hello-world", url)
    }

    @Test
    fun `buildFullUrl returns a full url when given a full url`() {
        val url = StringUtils.buildFullUrl("https://www.nhs.uk/hello-world")

        assertEquals("https://www.nhs.uk/hello-world", url)
    }

    @Test
    fun `collapseWhitespace removes extra whitespace from a string`() {
        val title = StringUtils.collapseWhitespace("Hello \r\n World \n")

        assertEquals("Hello World", title)
    }
}
