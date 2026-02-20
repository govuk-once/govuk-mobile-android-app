package uk.gov.govuk.navigation

import org.junit.Assert.assertEquals
import org.junit.Test

class TopLevelDestinationTest {

    @Test
    fun `Values includes Chat when chat is enabled`() {
        val result = TopLevelDestination.values(isChatEnabled = true)

        val expected = listOf(
            TopLevelDestination.Home,
            TopLevelDestination.Chat,
            TopLevelDestination.Settings
        )

        assertEquals(expected, result)
    }

    @Test
    fun `Values excludes Chat when chat is not enabled`() {
        val result = TopLevelDestination.values(isChatEnabled = false)

        val expected = listOf(
            TopLevelDestination.Home,
            TopLevelDestination.Settings
        )

        assertEquals(expected, result)
    }
}
