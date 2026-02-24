package uk.gov.govuk.config.data

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertThrows
import org.junit.Test
import uk.gov.govuk.config.data.remote.model.ChatBanner
import uk.gov.govuk.config.data.remote.model.Config
import uk.gov.govuk.config.data.remote.model.EmergencyBanner
import uk.gov.govuk.config.data.remote.model.TermsAndConditions
import uk.gov.govuk.config.data.remote.model.UserFeedbackBanner
import uk.gov.govuk.config.data.remote.source.FirebaseConfigDataSource
import uk.gov.govuk.config.data.remote.source.GovUkConfigDataSource
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.data.model.Result.Success

class ConfigRepoTest {

    private val govUkDataSource = mockk<GovUkConfigDataSource>(relaxed = true)
    private val firebaseDataSource = mockk<FirebaseConfigDataSource>(relaxed = true)
    private val config = mockk<Config>(relaxed = true)

    @Test
    fun `Given a successful config init, when config properties are requested, then return correct values`() {
        coEvery { govUkDataSource.fetchConfig() } returns Success(config)
        coEvery { firebaseDataSource.fetch() } returns true

        val repo = ConfigRepoImpl(govUkDataSource, firebaseDataSource)

        runTest {
            repo.initConfig()
            assertEquals(config.available, repo.isAvailable)
            assertEquals(config.minimumVersion, repo.minimumVersion)
            assertEquals(config.releaseFlags.search, repo.isSearchEnabled)
            assertEquals(config.chatUrls, repo.chatUrls)
        }
    }

    @Test
    fun `Given govuk config fetch fails, when initConfig, then return failure result`() = runTest {
        coEvery { govUkDataSource.fetchConfig() } returns Result.DeviceOffline()
        coEvery { firebaseDataSource.fetch() } returns true

        val repo = ConfigRepoImpl(govUkDataSource, firebaseDataSource)
        val result = repo.initConfig()

        assert(result is Result.DeviceOffline)
    }

    @Test
    fun `Given no config init, when any config property is requested, then throw exception`() {
        val repo = ConfigRepoImpl(govUkDataSource, firebaseDataSource)
        val exception = assertThrows(IllegalStateException::class.java) {
            repo.isAvailable
        }

        assertEquals("You must init config successfully before use!!!", exception.message)
    }

    @Test
    fun `When activateRemoteConfig is called, then activate firebase data source`() = runTest {
        coEvery { firebaseDataSource.activate() } returns true
        val repo = ConfigRepoImpl(govUkDataSource, firebaseDataSource)

        val result = repo.activateRemoteConfig()

        assert(result)
        coVerify { firebaseDataSource.activate() }
    }

    @Test
    fun `Given initialized config, when object properties are requested multiple times, they remain consistent`() {
        coEvery { govUkDataSource.fetchConfig() } returns Success(config)
        val repo = ConfigRepoImpl(govUkDataSource, firebaseDataSource)

        runTest {
            repo.initConfig()
            val firstCall = repo.chatUrls
            val secondCall = repo.chatUrls

            assertSame(firstCall, secondCall)
        }
    }

    @Test
    fun `Given a chat poll interval property, when retrieving the chat poll interval, then return property value`() = runTest {
        every { config.chatPollIntervalSeconds } returns 0.5
        coEvery { govUkDataSource.fetchConfig() } returns Success(config)

        val repo = ConfigRepoImpl(govUkDataSource, firebaseDataSource)
        repo.initConfig()

        assertEquals(0.5, repo.chatPollIntervalSeconds, 0.0)
    }

    @Test
    fun `Given the chat poll interval property is missing, when retrieving the chat poll interval, then return fallback value`() = runTest {
        every { config.chatPollIntervalSeconds } returns null
        coEvery { govUkDataSource.fetchConfig() } returns Success(config)

        val repo = ConfigRepoImpl(govUkDataSource, firebaseDataSource)
        repo.initConfig()

        assertEquals(3.0, repo.chatPollIntervalSeconds, 0.0)
    }

    @Test
    fun `Given the chat poll interval property is 0, when retrieving the chat poll interval, then return fallback value`() = runTest {
        every { config.chatPollIntervalSeconds } returns 0.0
        coEvery { govUkDataSource.fetchConfig() } returns Success(config)

        val repo = ConfigRepoImpl(govUkDataSource, firebaseDataSource)
        repo.initConfig()

        assertEquals(3.0, repo.chatPollIntervalSeconds, 0.0)
    }

    @Test
    fun `Given successful init, when accessing remaining properties, then return correct config values`() = runTest {
        val mockBanners = listOf(mockk<EmergencyBanner>())
        val mockFeedback = mockk<UserFeedbackBanner>()
        val mockChatBanner = mockk<ChatBanner>()
        val mockTerms = mockk<TermsAndConditions>()

        every { config.recommendedVersion } returns "2.0.0"
        every { config.releaseFlags.recentActivity } returns true
        every { config.releaseFlags.topics } returns true
        every { config.releaseFlags.notifications } returns true
        every { config.releaseFlags.localServices } returns true
        every { config.releaseFlags.externalBrowser } returns true
        every { config.refreshTokenExpirySeconds } returns 3600L
        every { config.emergencyBanners } returns mockBanners
        every { config.userFeedbackBanner } returns mockFeedback
        every { config.chatBanner } returns mockChatBanner
        every { config.termsAndConditions } returns mockTerms
        coEvery { govUkDataSource.fetchConfig() } returns Success(config)
        coEvery { firebaseDataSource.fetch() } returns true

        val repo = ConfigRepoImpl(govUkDataSource, firebaseDataSource)
        repo.initConfig()

        assertEquals("2.0.0", repo.recommendedVersion)
        assertEquals(true, repo.isRecentActivityEnabled)
        assertEquals(true, repo.isTopicsEnabled)
        assertEquals(true, repo.isNotificationsEnabled)
        assertEquals(true, repo.isLocalServicesEnabled)
        assertEquals(true, repo.isExternalBrowserEnabled)
        assertEquals(3600L, repo.refreshTokenExpirySeconds)
        assertSame(mockBanners, repo.emergencyBanners)
        assertSame(mockFeedback, repo.userFeedbackBanner)
        assertSame(mockChatBanner, repo.chatBanner)
        assertSame(mockTerms, repo.termsAndConditions)
    }
}
