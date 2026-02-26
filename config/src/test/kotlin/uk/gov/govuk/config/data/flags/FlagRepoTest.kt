package uk.gov.govuk.config.data.flags

import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.config.data.ConfigRepo

class FlagRepoTest {

    private val debugFlags = mockk<DebugFlags>(relaxed = true)
    private val configRepo = mockk<ConfigRepo>(relaxed = true)

    private lateinit var flagRepo: FlagRepo

    @Before
    fun setup() {
        flagRepo = FlagRepo(
            debugEnabled = true,
            debugFlags = debugFlags,
            configRepo = configRepo
        )
    }

    @After
    fun tearDown() {
        // Remove mocks to prevent side effects
        unmockkAll()
    }

    @Test
    fun `Given debug is enabled, debug flag is true and remote flag is true, When is enabled, then return true`() {
        val enabled = isEnabled(
            debugEnabled = true,
            debugFlag = true,
            remoteFlag = true
        )

        assertTrue(enabled)
    }

    @Test
    fun `Given debug is enabled, debug flag is true and remote flag is false, When is enabled, then return true`() {
        val enabled = isEnabled(
            debugEnabled = true,
            debugFlag = true,
            remoteFlag = false
        )

        assertTrue(enabled)
    }

    @Test
    fun `Given debug is enabled, debug flag is false and remote flag is true, When is enabled, then return false`() {
        val enabled = isEnabled(
            debugEnabled = true,
            debugFlag = false,
            remoteFlag = true
        )

        assertFalse(enabled)
    }

    @Test
    fun `Given debug is enabled, debug flag is false and remote flag is false, When is enabled, then return false`() {
        val enabled = isEnabled(
            debugEnabled = true,
            debugFlag = false,
            remoteFlag = false
        )

        assertFalse(enabled)
    }

    @Test
    fun `Given debug is enabled, debug flag is unset and remote flag is true, When is enabled, then return true`() {
        val enabled = isEnabled(
            debugEnabled = true,
            debugFlag = null,
            remoteFlag = true
        )

        assertTrue(enabled)
    }

    @Test
    fun `Given debug is enabled, debug flag is unset and remote flag is false, When is enabled, then return false`() {
        val enabled = isEnabled(
            debugEnabled = true,
            debugFlag = null,
            remoteFlag = false
        )

        assertFalse(enabled)
    }

    @Test
    fun `Given debug is disabled, debug flag is true and remote flag is true, When is enabled, then return true`() {
        val enabled = isEnabled(
            debugEnabled = false,
            debugFlag = true,
            remoteFlag = true
        )

        assertTrue(enabled)
    }

    @Test
    fun `Given debug is disabled, debug flag is true and remote flag is false, When is enabled, then return false`() {
        val enabled = isEnabled(
            debugEnabled = false,
            debugFlag = true,
            remoteFlag = false
        )

        assertFalse(enabled)
    }

    @Test
    fun `Given debug is disabled, debug flag is false and remote flag is true, When is enabled, then return true`() {
        val enabled = isEnabled(
            debugEnabled = false,
            debugFlag = false,
            remoteFlag = true
        )

        assertTrue(enabled)
    }

    @Test
    fun `Given debug is disabled, debug flag is false and remote flag is false, When is enabled, then return false`() {
        val enabled = isEnabled(
            debugEnabled = false,
            debugFlag = false,
            remoteFlag = false
        )

        assertFalse(enabled)
    }

    @Test
    fun `Given debug is disabled, debug flag is unset and remote flag is true, When is enabled, then return true`() {
        val enabled = isEnabled(
            debugEnabled = false,
            debugFlag = null,
            remoteFlag = true
        )

        assertTrue(enabled)
    }

    @Test
    fun `Given debug is disabled, debug flag is unset and remote flag is false, When is enabled, then return false`() {
        val enabled = isEnabled(
            debugEnabled = false,
            debugFlag = null,
            remoteFlag = false
        )

        assertFalse(enabled)
    }

    @Test
    fun `Given app is available, When available is true, then return true`() {
        mockkStatic(::isEnabled)
        every { isEnabled(any(), any(), any()) } returns true

        assertTrue(flagRepo.isAppAvailable())
    }

    @Test
    fun `Given app is unavailable, When is app available is false, then return false`() {
        mockkStatic(::isEnabled)
        every { isEnabled(any(), any(), any()) } returns false

        assertFalse(flagRepo.isAppAvailable())
    }

    @Test
    fun `Given the debug minimum version is 0_0_2, When the app version is 0_0_1, then return true`() {
        every { debugFlags.minimumVersion } returns "0.0.2"

        assertTrue(flagRepo.isForcedUpdate("0.0.1"))
    }

    @Test
    fun `Given the debug minimum version is 0_0_2, When the app version is 0_0_2, then return false`() {
        every { debugFlags.minimumVersion } returns "0.0.2"

        assertFalse(flagRepo.isForcedUpdate("0.0.2"))
    }

    @Test
    fun `Given the debug minimum version is 0_0_1, When the app version is 0_0_2, then return false`() {
        every { debugFlags.minimumVersion } returns "0.0.1"

        assertFalse(flagRepo.isForcedUpdate("0.0.2"))
    }

    @Test
    fun `Given the remote minimum version is 0_0_2, When the app version is 0_0_1, then return true`() {
        every { debugFlags.minimumVersion } returns null
        every { configRepo.minimumVersion } returns "0.0.2"

        assertTrue(flagRepo.isForcedUpdate("0.0.1"))
    }

    @Test
    fun `Given the remote minimum version is 0_0_2, When the app version is 0_0_2, then return false`() {
        every { debugFlags.minimumVersion } returns null
        every { configRepo.minimumVersion } returns "0.0.2"

        assertFalse(flagRepo.isForcedUpdate("0.0.2"))
    }

    @Test
    fun `Given the remote minimum version is 0_0_1, When the app version is 0_0_2, then return false`() {
        every { debugFlags.minimumVersion } returns null
        every { configRepo.minimumVersion } returns "0.0.1"

        assertFalse(flagRepo.isForcedUpdate("0.0.2"))
    }

    @Test
    fun `Given the debug recommended version is 0_0_2, When the app version is 0_0_1, then return true`() {
        every { debugFlags.recommendedVersion } returns "0.0.2"

        assertTrue(flagRepo.isRecommendUpdate("0.0.1"))
    }

    @Test
    fun `Given the debug recommended version is 0_0_2, When the app version is 0_0_2, then return false`() {
        every { debugFlags.recommendedVersion } returns "0.0.2"

        assertFalse(flagRepo.isRecommendUpdate("0.0.2"))
    }

    @Test
    fun `Given the debug recommended version is 0_0_1, When the app version is 0_0_2, then return false`() {
        every { debugFlags.recommendedVersion } returns "0.0.1"

        assertFalse(flagRepo.isRecommendUpdate("0.0.2"))
    }

    @Test
    fun `Given the remote recommended version is 0_0_2, When the app version is 0_0_1, then return true`() {
        every { debugFlags.recommendedVersion } returns null
        every { configRepo.recommendedVersion } returns "0.0.2"

        assertTrue(flagRepo.isRecommendUpdate("0.0.1"))
    }

    @Test
    fun `Given the remote recommended version is 0_0_2, When the app version is 0_0_2, then return false`() {
        every { debugFlags.recommendedVersion } returns null
        every { configRepo.recommendedVersion } returns "0.0.2"

        assertFalse(flagRepo.isRecommendUpdate("0.0.2"))
    }

    @Test
    fun `Given the remote recommended version is 0_0_1, When the app version is 0_0_2, then return false`() {
        every { debugFlags.recommendedVersion } returns null
        every { configRepo.recommendedVersion } returns "0.0.1"

        assertFalse(flagRepo.isRecommendUpdate("0.0.2"))
    }

    @Test
    fun `Given search is enabled, When is search enabled, then return true`() {
        mockkStatic(::isEnabled)
        every { isEnabled(any(), any(), any()) } returns true

        assertTrue(flagRepo.isSearchEnabled())
    }

    @Test
    fun `Given search is disabled, When is search enabled, then return false`() {
        mockkStatic(::isEnabled)
        every { isEnabled(any(), any(), any()) } returns false

        assertFalse(flagRepo.isSearchEnabled())
    }

    @Test
    fun `Given recent activity is enabled, When is recent activity enabled, then return true`() {
        mockkStatic(::isEnabled)
        every { isEnabled(any(), any(), any()) } returns true

        assertTrue(flagRepo.isRecentActivityEnabled())
    }

    @Test
    fun `Given recent activity is disabled, When is recent activity disabled, then return false`() {
        mockkStatic(::isEnabled)
        every { isEnabled(any(), any(), any()) } returns false

        assertFalse(flagRepo.isRecentActivityEnabled())
    }

    @Test
    fun `Given topics is enabled, When is topics enabled, then return true`() {
        mockkStatic(::isEnabled)
        every { isEnabled(any(), any(), any()) } returns true

        assertTrue(flagRepo.isTopicsEnabled())
    }

    @Test
    fun `Given topics is disabled, When is topics enabled, then return false`() {
        mockkStatic(::isEnabled)
        every { isEnabled(any(), any(), any()) } returns false

        assertFalse(flagRepo.isTopicsEnabled())
    }

    @Test
    fun `Given notifications is enabled, When is notifications enabled, then return true`() {
        mockkStatic(::isEnabled)
        every { isEnabled(any(), any(), any()) } returns true

        assertTrue(flagRepo.isNotificationsEnabled())
    }

    @Test
    fun `Given notifications is disabled, When is notifications enabled, then return false`() {
        mockkStatic(::isEnabled)
        every { isEnabled(any(), any(), any()) } returns false

        assertFalse(flagRepo.isNotificationsEnabled())
    }

    @Test
    fun `Given local is enabled, When is local enabled, then return true`() {
        mockkStatic(::isEnabled)
        every { isEnabled(any(), any(), any()) } returns true

        assertTrue(flagRepo.isLocalServicesEnabled())
    }

    @Test
    fun `Given local is disabled, When is local enabled, then return false`() {
        mockkStatic(::isEnabled)
        every { isEnabled(any(), any(), any()) } returns false

        assertFalse(flagRepo.isLocalServicesEnabled())
    }

    @Test
    fun `Given a release build, When chat is enabled, then return false`() {
        flagRepo = FlagRepo(false, debugFlags, configRepo)

        assertFalse(flagRepo.isChatEnabled())
    }

    @Test
    fun `Given a debug build, When is external browser enabled, then return false`() {
        flagRepo = FlagRepo(true, debugFlags, configRepo)

        assertFalse(flagRepo.isExternalBrowserEnabled())
    }

    @Test
    fun `Given a release build, When is external browser enabled, then return false`() {
        flagRepo = FlagRepo(false, debugFlags, configRepo)

        assertFalse(flagRepo.isExternalBrowserEnabled())
    }
}
