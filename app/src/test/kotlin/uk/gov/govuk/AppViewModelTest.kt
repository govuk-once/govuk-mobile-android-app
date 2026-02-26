package uk.gov.govuk

import androidx.navigation.NavController
import io.mockk.clearAllMocks
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.yield
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withContext
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.AppViewModel.TimeoutEvent
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.chat.ChatFeature
import uk.gov.govuk.config.data.ConfigRepo
import uk.gov.govuk.config.data.flags.FlagRepo
import uk.gov.govuk.config.data.remote.model.ChatBanner
import uk.gov.govuk.config.data.remote.model.EmergencyBanner
import uk.gov.govuk.config.data.remote.model.EmergencyBannerType
import uk.gov.govuk.config.data.remote.model.Link
import uk.gov.govuk.config.data.remote.model.UserFeedbackBanner
import uk.gov.govuk.data.AppRepo
import uk.gov.govuk.data.auth.AuthRepo
import uk.gov.govuk.data.model.Result.Error
import uk.gov.govuk.data.model.Result.InvalidSignature
import uk.gov.govuk.data.model.Result.Success
import uk.gov.govuk.login.data.LoginRepo
import uk.gov.govuk.navigation.AppNavigation
import uk.gov.govuk.search.SearchFeature
import uk.gov.govuk.terms.data.TermsRepo
import uk.gov.govuk.topics.TopicsFeature
import uk.gov.govuk.visited.Visited
import uk.gov.govuk.widgets.model.HomeWidget
import uk.govuk.app.local.LocalFeature
import kotlin.coroutines.cancellation.CancellationException

@OptIn(ExperimentalCoroutinesApi::class)
class AppViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private val timeoutManager = mockk<TimeoutManager>(relaxed = true)
    private val appRepo = mockk<AppRepo>(relaxed = true)
    private val loginRepo = mockk<LoginRepo>(relaxed = true)
    private val termsRepo = mockk<TermsRepo>(relaxed = true)
    private val configRepo = mockk<ConfigRepo>(relaxed = true)
    private val flagRepo = mockk<FlagRepo>(relaxed = true)
    private val authRepo = mockk<AuthRepo>(relaxed = true)
    private val topicsFeature = mockk<TopicsFeature>(relaxed = true)
    private val localFeature = mockk<LocalFeature>(relaxed = true)
    private val searchFeature = mockk<SearchFeature>(relaxed = true)
    private val visited = mockk<Visited>(relaxed = true)
    private val chatFeature = mockk<ChatFeature>(relaxed = true)
    private val analyticsClient = mockk<AnalyticsClient>(relaxed = true)
    private val appNavigation = mockk<AppNavigation>(relaxed = true)
    private val navController = mockk<NavController>(relaxed = true)

    private lateinit var viewModel: AppViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)

        // Default setup state, overridden by individual tests if/when required
        coEvery { configRepo.initConfig() } returns Success(Unit)
        every { localFeature.hasLocalAuthority() } returns flowOf(false)
        every { appRepo.suppressedHomeWidgets } returns flowOf(emptySet())
        every { flagRepo.isAppAvailable() } returns true

        viewModel = AppViewModel(timeoutManager, appRepo, loginRepo, termsRepo, configRepo, flagRepo, authRepo, topicsFeature,
            localFeature, searchFeature, visited, chatFeature, analyticsClient, appNavigation)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Given there is an error when retrieving the remote config, When init, then should display app unavailable`() {
        coEvery { configRepo.initConfig() } returns Error()

        val viewModel = AppViewModel(timeoutManager, appRepo, loginRepo, termsRepo, configRepo, flagRepo, authRepo, topicsFeature,
            localFeature, searchFeature, visited, chatFeature, analyticsClient, appNavigation)

        runTest {
            val result = viewModel.uiState.first()
            assertTrue(result is AppUiState.AppUnavailable)
        }
    }

    @Test
    fun `Given the config signature is invalid, When init, then should display forced update`() {
        coEvery { configRepo.initConfig() } returns InvalidSignature()

        val viewModel = AppViewModel(timeoutManager, appRepo, loginRepo, termsRepo, configRepo, flagRepo, authRepo, topicsFeature,
            localFeature, searchFeature, visited, chatFeature, analyticsClient, appNavigation)

        runTest {
            val result = viewModel.uiState.first()
            assertTrue(result is AppUiState.ForcedUpdate)
        }
    }

    @Test
    fun `Given the app is unavailable, When init, then should display app unavailable`() {
        every { flagRepo.isAppAvailable() } returns false

        val viewModel = AppViewModel(timeoutManager, appRepo, loginRepo, termsRepo, configRepo, flagRepo, authRepo, topicsFeature,
            localFeature, searchFeature, visited, chatFeature, analyticsClient, appNavigation)

        runTest {
            val result = viewModel.uiState.first()
            assertTrue(result is AppUiState.AppUnavailable)
        }
    }

    @Test
    fun `Given the app is available, When init, then should not display app unavailable`() {
        runTest {
            val result = viewModel.uiState.first()
            assertTrue(result is AppUiState.Default)
        }
    }

    @Test
    fun `Given forced update, When init, then should display forced update`() {
        every { flagRepo.isForcedUpdate(any()) } returns true

        val viewModel = AppViewModel(timeoutManager, appRepo, loginRepo, termsRepo, configRepo, flagRepo, authRepo, topicsFeature,
            localFeature, searchFeature, visited, chatFeature, analyticsClient, appNavigation)

        runTest {
            val result = viewModel.uiState.first()
            assertTrue(result is AppUiState.ForcedUpdate)
        }
    }

    @Test
    fun `Given don't forced update, When init, then should not display forced update`() {
        every { flagRepo.isForcedUpdate(any()) } returns false

        val viewModel = AppViewModel(timeoutManager, appRepo, loginRepo, termsRepo, configRepo, flagRepo, authRepo, topicsFeature,
            localFeature, searchFeature, visited, chatFeature, analyticsClient, appNavigation)

        runTest {
            val result = viewModel.uiState.first()
            assertTrue(result is AppUiState.Default)
        }
    }

    @Test
    fun `Given recommend update, When init, then should display recommend update`() {
        every { flagRepo.isRecommendUpdate(any()) } returns true

        val viewModel = AppViewModel(timeoutManager, appRepo, loginRepo, termsRepo, configRepo, flagRepo, authRepo, topicsFeature,
            localFeature, searchFeature, visited, chatFeature, analyticsClient, appNavigation)

        runTest {
            val result = viewModel.uiState.first() as AppUiState.Default
            assertTrue(result.shouldDisplayRecommendUpdate)
        }
    }

    @Test
    fun `Given don't recommend update, When init, then should not display recommend update`() {
        every { flagRepo.isRecommendUpdate(any()) } returns false

        val viewModel = AppViewModel(timeoutManager, appRepo, loginRepo, termsRepo, configRepo, flagRepo, authRepo, topicsFeature,
            localFeature, searchFeature, visited, chatFeature, analyticsClient, appNavigation)

        runTest {
            val result = viewModel.uiState.first() as AppUiState.Default
            assertFalse(result.shouldDisplayRecommendUpdate)
        }
    }

    @Test
    fun `Given external browser enabled, When init, then should show external browser`() {
        every { flagRepo.isExternalBrowserEnabled() } returns true

        val viewModel = AppViewModel(timeoutManager, appRepo, loginRepo, termsRepo, configRepo, flagRepo, authRepo, topicsFeature,
            localFeature, searchFeature, visited, chatFeature, analyticsClient, appNavigation)

        runTest {
            val result = viewModel.uiState.first() as AppUiState.Default
            assertTrue(result.shouldShowExternalBrowser)
        }
    }

    @Test
    fun `Given external browser enabled is false, When init, then should not show external browser`() {
        every { flagRepo.isExternalBrowserEnabled() } returns false

        val viewModel = AppViewModel(timeoutManager, appRepo, loginRepo, termsRepo, configRepo, flagRepo, authRepo, topicsFeature,
            localFeature, searchFeature, visited, chatFeature, analyticsClient, appNavigation)

        runTest {
            val result = viewModel.uiState.first() as AppUiState.Default
            assertFalse(result.shouldShowExternalBrowser)
        }
    }

    @Test
    fun `Given the search feature is enabled, When init, then emit search enabled state`() {
        coEvery { flagRepo.isSearchEnabled() } returns true

        val viewModel = AppViewModel(timeoutManager, appRepo, loginRepo, termsRepo, configRepo, flagRepo, authRepo, topicsFeature,
            localFeature, searchFeature, visited, chatFeature, analyticsClient, appNavigation)

        runTest {
            viewModel.homeWidgets.first()
                ?.let { assertTrue(it.contains(HomeWidget.Search)) }
        }
    }


    @Test
    fun `Given the search feature is disabled, When init, then emit search disabled state`() {
        coEvery { flagRepo.isSearchEnabled() } returns false

        val viewModel = AppViewModel(timeoutManager, appRepo, loginRepo, termsRepo, configRepo, flagRepo, authRepo, topicsFeature,
            localFeature, searchFeature, visited, chatFeature, analyticsClient, appNavigation)

        runTest {
            viewModel.homeWidgets.first()
                ?.let { assertFalse(it.contains(HomeWidget.Search)) }
        }
    }

    @Test
    fun `Given the recent activity feature is enabled, When init, then emit recent activity enabled state`() {
        coEvery { flagRepo.isRecentActivityEnabled() } returns true

        val viewModel = AppViewModel(timeoutManager, appRepo, loginRepo, termsRepo, configRepo, flagRepo, authRepo, topicsFeature,
            localFeature, searchFeature, visited, chatFeature, analyticsClient, appNavigation)

        runTest {
            viewModel.homeWidgets.first()
                ?.let { assertTrue(it.contains(HomeWidget.RecentActivity)) }
        }
    }

    @Test
    fun `Given the recent activity feature is disabled, When init, then emit recent activity disabled state`() {
        coEvery { flagRepo.isRecentActivityEnabled() } returns false

        val viewModel = AppViewModel(timeoutManager, appRepo, loginRepo, termsRepo, configRepo, flagRepo, authRepo, topicsFeature,
            localFeature, searchFeature, visited, chatFeature, analyticsClient, appNavigation)

        runTest {
            viewModel.homeWidgets.first()
                ?.let { assertFalse(it.contains(HomeWidget.RecentActivity)) }
        }
    }

    @Test
    fun `Given the topics feature is enabled, When init, then emit topics enabled state`() {
        coEvery { flagRepo.isTopicsEnabled() } returns true

        val viewModel = AppViewModel(timeoutManager, appRepo, loginRepo, termsRepo, configRepo, flagRepo, authRepo, topicsFeature,
            localFeature, searchFeature, visited, chatFeature, analyticsClient, appNavigation)

        runTest {
            viewModel.homeWidgets.first()
                ?.let { assertTrue(it.contains(HomeWidget.Topics)) }
        }
    }

    @Test
    fun `Given the topics feature is disabled, When init, then emit topics disabled state`() {
        coEvery { flagRepo.isTopicsEnabled() } returns false

        val viewModel = AppViewModel(timeoutManager, appRepo, loginRepo, termsRepo, configRepo, flagRepo, authRepo, topicsFeature,
            localFeature, searchFeature, visited, chatFeature, analyticsClient, appNavigation)

        runTest {
            viewModel.homeWidgets.first()
                ?.let { assertFalse(it.contains(HomeWidget.Topics)) }
        }
    }

    @Test
    fun `When topic selection completed, then call repo topic selection completed`() = runTest(dispatcher) {
        coEvery { appRepo.topicSelectionCompleted() } coAnswers { withContext(Dispatchers.Default) {} }
        coEvery { appNavigation.onNext(any()) } coAnswers { withContext(Dispatchers.Default) {} }

        viewModel.topicSelectionCompleted(navController)

        advanceUntilIdle()

        coVerify {
            appRepo.topicSelectionCompleted()
            appNavigation.onNext(navController)
        }
    }

    @Test
    fun `When tab is clicked, then log analytics`() {
        runTest {
            viewModel.onTabClick("text")

            coVerify {
                analyticsClient.tabClick("text")
            }
        }
    }

    @Test
    fun `When an external widget is clicked, then log analytics`() {
        runTest {
            viewModel.onWidgetClick("text", "url", true, "section")

            coVerify {
                analyticsClient.widgetClick("text", "url", true, "section")
            }
        }
    }

    @Test
    fun `When an internal widget is clicked, then log analytics`() {
        runTest {
            viewModel.onWidgetClick("text", "url", false, "section")

            coVerify(exactly = 1) {
                analyticsClient.widgetClick("text", "url", false, "section")
            }
        }
    }

    @Test
    fun `When an suppress widget is clicked, then log analytics`() {
        runTest {
            viewModel.onSuppressWidgetClick("id", "text", "section")

            coVerify {
                appRepo.suppressHomeWidget("id")
                analyticsClient.suppressWidgetClick("text", "section")
            }
        }
    }

    @Test
    fun `Given a deep link is received, When the app has the deep link, then log analytics`() {
        runTest {
            viewModel.onDeepLinkReceived(true, "url")

            coVerify {
                analyticsClient.deepLinkEvent(true, "url")
            }
        }
    }

    @Test
    fun `Given a deep link is received, When the app doesn't have the deep link, then log analytics`() {
        runTest {
            viewModel.onDeepLinkReceived(false, "url")

            coVerify {
                analyticsClient.deepLinkEvent(false, "url")
            }
        }
    }

    @Test
    fun `Given the user tries again, then emit loading state and fetch config`() {
        runTest {
            val uiStates = mutableListOf<AppUiState?>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.uiState.toList(uiStates)
            }

            clearMocks(configRepo)
            coEvery { configRepo.initConfig() } returns Success(Unit)

            viewModel.onTryAgain()
            assertTrue(uiStates[1] is AppUiState.Loading)
            coVerify {
                configRepo.initConfig()
            }
        }
    }

    @Test
    fun `Given the config has no chat banner, When init, then home widgets do not contain a chat banner`() {
        every { configRepo.chatBanner } returns null

        val viewModel = AppViewModel(timeoutManager, appRepo, loginRepo, termsRepo, configRepo, flagRepo, authRepo, topicsFeature, localFeature,
            searchFeature, visited, chatFeature, analyticsClient, appNavigation)

        runTest {
            val homeWidgets = viewModel.homeWidgets.value!!
            assertFalse(homeWidgets.any { it is HomeWidget.Chat })
        }
    }

    @Test
    fun `Given the chat feature is disabled, When init, then home widgets do not contain a chat banner`() {
        every { configRepo.chatBanner } returns ChatBanner("", "", "", Link("", ""))
        coEvery { flagRepo.isChatEnabled() } returns false

        val viewModel = AppViewModel(timeoutManager, appRepo, loginRepo, termsRepo, configRepo, flagRepo, authRepo, topicsFeature, localFeature,
            searchFeature, visited, chatFeature, analyticsClient, appNavigation)

        runTest {
            val homeWidgets = viewModel.homeWidgets.value!!
            assertFalse(homeWidgets.any { it is HomeWidget.Chat })
        }
    }

    @Test
    fun `Given the chat banner has been suppressed, When init, then home widgets do not contain a chat banner`() {
        every { configRepo.chatBanner } returns ChatBanner("chat_banner_id", "", "", Link("", ""))
        coEvery { flagRepo.isChatEnabled() } returns true
        every { appRepo.suppressedHomeWidgets } returns flowOf(setOf("chat_banner_id"))

        val viewModel = AppViewModel(timeoutManager, appRepo, loginRepo, termsRepo, configRepo, flagRepo, authRepo, topicsFeature, localFeature,
            searchFeature, visited, chatFeature, analyticsClient, appNavigation)

        runTest {
            val homeWidgets = viewModel.homeWidgets.value!!
            assertFalse(homeWidgets.any { it is HomeWidget.Chat })
        }
    }

    @Test
    fun `Given a chat banner, When init, then home widgets contains a chat banner`() {
        val chatBanner = ChatBanner(
            "id",
            "title",
            "body",
            Link(
                "title",
                "url"
            )
        )
        every { configRepo.chatBanner } returns chatBanner
        coEvery { flagRepo.isChatEnabled() } returns true

        val viewModel = AppViewModel(timeoutManager, appRepo, loginRepo, termsRepo, configRepo, flagRepo, authRepo, topicsFeature, localFeature,
            searchFeature, visited, chatFeature, analyticsClient, appNavigation)

        runTest {
            val homeWidgets = viewModel.homeWidgets.value!!
            assertTrue(homeWidgets.any { it == HomeWidget.Chat(chatBanner) })
        }
    }

    @Test
    fun `Given the local feature is disabled, When init, then emit local disabled state`() {
        coEvery { flagRepo.isLocalServicesEnabled() } returns false

        val viewModel = AppViewModel(timeoutManager, appRepo, loginRepo, termsRepo, configRepo, flagRepo, authRepo, topicsFeature, localFeature,
            searchFeature, visited, chatFeature, analyticsClient, appNavigation)

        runTest {
            viewModel.homeWidgets.first()
                ?.let { assertFalse(it.contains(HomeWidget.Local)) }
        }
    }

    @Test
    fun `Given the local feature is enabled and a local authority is not selected, When init, then emit local enabled state`() {
        coEvery { flagRepo.isLocalServicesEnabled() } returns true
        coEvery { flagRepo.isTopicsEnabled() } returns true

        val viewModel = AppViewModel(timeoutManager, appRepo, loginRepo, termsRepo, configRepo, flagRepo, authRepo, topicsFeature, localFeature,
            searchFeature, visited, chatFeature, analyticsClient, appNavigation)

        runTest {
            val homeWidgets = viewModel.homeWidgets.value!!
            assertEquals(HomeWidget.Topics, homeWidgets[0])
            assertEquals(HomeWidget.Local, homeWidgets[1])
        }
    }

    @Test
    fun `Given the local feature is enabled and a local authority is selected, When init, then emit local enabled state`() {
        every { configRepo.userFeedbackBanner } returns null
        coEvery { flagRepo.isLocalServicesEnabled() } returns true
        coEvery { flagRepo.isTopicsEnabled() } returns true
        every { localFeature.hasLocalAuthority() } returns flowOf(true)

        val viewModel = AppViewModel(timeoutManager, appRepo, loginRepo, termsRepo, configRepo, flagRepo, authRepo, topicsFeature, localFeature,
            searchFeature, visited, chatFeature, analyticsClient, appNavigation)

        runTest {
            val homeWidgets = viewModel.homeWidgets.value!!
            assertEquals(HomeWidget.Topics, homeWidgets[0])
            assertEquals(HomeWidget.Local, homeWidgets[1])
        }
    }

    @Test
    fun `Given the config has a user feedback banner, When init, then user feedback is the last home widget`() {
        val userFeedbackBanner = UserFeedbackBanner("body", Link("title", "url"))
        coEvery { flagRepo.isLocalServicesEnabled() } returns true
        every { configRepo.userFeedbackBanner } returns userFeedbackBanner

        val viewModel = AppViewModel(timeoutManager, appRepo, loginRepo, termsRepo, configRepo, flagRepo, authRepo, topicsFeature, localFeature,
            searchFeature, visited, chatFeature, analyticsClient, appNavigation)

        runTest {
            val homeWidgets = viewModel.homeWidgets.value!!
            val userFeedbackWidget = HomeWidget.UserFeedback(userFeedbackBanner)
            assertEquals(userFeedbackWidget, homeWidgets.last())
        }
    }

    @Test
    fun `Given a new user or the same user has logged in, When on login, then navigate to next nav destination`() {
        coEvery { authRepo.isDifferentUser() } returns false

        runTest {
            viewModel.onLogin(navController)

            coVerify(exactly = 0) {
                authRepo.clear()
                appRepo.clear()
                loginRepo.clear()
                termsRepo.clear()
                topicsFeature.clear()
                localFeature.clear()
                searchFeature.clear()
                visited.clear()
                chatFeature.clear()
                analyticsClient.clear()
                configRepo.clearRemoteConfigValues()
            }

            coVerify {
                appNavigation.onNext(navController)
            }
        }
    }

    @Test
    fun `Given a different user has logged in, When on login, then clear data and navigate to next destination`() {
        coEvery { authRepo.isDifferentUser() } returns true

        runTest {
            viewModel.onLogin(navController)

            coVerify {
                authRepo.clear()
                appRepo.clear()
                loginRepo.clear()
                topicsFeature.clear()
                localFeature.clear()
                searchFeature.clear()
                visited.clear()
                chatFeature.clear()
                analyticsClient.clear()
                appNavigation.onNext(navController)
            }
        }
    }

    @Test
    fun `Given a user has interacted with the app, When on user interaction, then call timeout manager`() {
        viewModel.onUserInteraction( 0L)

        verify {
            timeoutManager.onUserInteraction(0L, onWarning = any(), onTimeout = any())
        }
    }

    @Test
    fun `Given user session is not active, When the app times out, do nothing`() {
        clearAllMocks()

        val slot = slot<(() -> Unit)>()
        every { timeoutManager.onUserInteraction(any(), onWarning = any(), onTimeout = capture(slot)) } returns Unit
        every { authRepo.isUserSessionActive() } returns false

        viewModel.onUserInteraction(0L)
        slot.captured.invoke()

        coVerify(exactly = 0) {
            authRepo.endUserSession()
            appNavigation.onSignOut(any())
        }
    }

    @Test
    fun `Given a user session is active, When the app is about to time out, emit warning`() = runTest {
        clearAllMocks()

        val slot = slot<(() -> Unit)>()
        every { timeoutManager.onUserInteraction(any(), onWarning = capture(slot), onTimeout = any()) } returns Unit
        every { authRepo.isUserSessionActive() } returns true

        viewModel.onUserInteraction(0L)
        slot.captured.invoke()

        val event = viewModel.timeOutEvent.first()
        assertEquals(TimeoutEvent.WARNING, event)

        verify(exactly = 0) {
            authRepo.endUserSession()
            appNavigation.onSignOut(any())
        }
    }

    @Test
    fun `Given a user session is active, When the app times out, end user session and emit timeout`() = runTest {
        clearAllMocks()

        val slot = slot<(() -> Unit)>()
        every { timeoutManager.onUserInteraction(any(), onWarning = any(), onTimeout = capture(slot)) } returns Unit
        every { authRepo.isUserSessionActive() } returns true

        viewModel.onUserInteraction(0L)
        slot.captured.invoke()

        val event = viewModel.timeOutEvent.first()
        assertEquals(TimeoutEvent.TIMEOUT, event)

        coVerify {
            authRepo.endUserSession()
        }

        verify(exactly = 0) {
            appNavigation.onSignOut(any())
        }
    }

    @Test
    fun `Given chat remote and local flags are true, When init, then should show chat`() = runTest {
        every { flagRepo.isChatEnabled() } returns true

        val viewModel = AppViewModel(
            timeoutManager, appRepo, loginRepo, termsRepo, configRepo, flagRepo, authRepo, topicsFeature,
            localFeature, searchFeature, visited, chatFeature, analyticsClient, appNavigation
        )

        val uiState = viewModel.uiState.first() as AppUiState.Default
        assertTrue(uiState.isChatEnabled)
    }

    @Test
    fun `Given chat remote flag is true and the local flag is false, When init, then do not show chat`() = runTest {
        every { flagRepo.isChatEnabled() } returns false

        val viewModel = AppViewModel(
            timeoutManager, appRepo, loginRepo, termsRepo, configRepo, flagRepo, authRepo, topicsFeature,
            localFeature, searchFeature, visited, chatFeature, analyticsClient, appNavigation
        )

        val uiState = viewModel.uiState.first() as AppUiState.Default
        assertFalse(uiState.isChatEnabled)
    }

    @Test
    fun `Given analytics is enabled, when analytics consent completed, then refresh remote config`() = runTest(dispatcher) {
        every { analyticsClient.isAnalyticsEnabled() } returns true

        coEvery { configRepo.refreshRemoteConfig() } coAnswers { withContext(Dispatchers.Default) {} }
        coEvery { appNavigation.onNext(any()) } coAnswers { withContext(Dispatchers.Default) {} }

        viewModel.onAnalyticsConsentCompleted(navController)
        advanceUntilIdle()

        coVerify {
            configRepo.refreshRemoteConfig()
            appNavigation.onNext(navController)
        }
    }

    @Test
    fun `Given analytics is disabled, when analytics consent completed, then do not activate remote config`() = runTest(dispatcher) {
        every { analyticsClient.isAnalyticsEnabled() } returns false

        coEvery { appNavigation.onNext(any()) } coAnswers { withContext(Dispatchers.Default) {} }

        viewModel.onAnalyticsConsentCompleted(navController)
        advanceUntilIdle()

        coVerify(exactly = 0) { configRepo.refreshRemoteConfig() }
        coVerify(exactly = 0) { configRepo.activateRemoteConfig() }
        coVerify { appNavigation.onNext(navController) }
    }

    @Test
    fun `Given a returning user with analytics enabled, when initialising with config, then activate remote config`() {
        every { analyticsClient.isAnalyticsEnabled() } returns true

        val viewModel = AppViewModel(timeoutManager, appRepo, loginRepo, termsRepo, configRepo, flagRepo, authRepo, topicsFeature,
            localFeature, searchFeature, visited, chatFeature, analyticsClient, appNavigation)

        runTest {
            viewModel.uiState.first()
            coVerify { configRepo.activateRemoteConfig() }
        }
    }

    @Test
    fun `Given multiple emergency banners where one is suppressed, When init, then show only unsuppressed banners`() {
        val banner1 = EmergencyBanner(
            id = "id1",
            title = "Title 1",
            body = "Body 1",
            link = Link("Link Title", "http://url1"),
            type = EmergencyBannerType.INFORMATION,
            allowsDismissal = true,
            dismissAltText = "Dismiss"
        )

        val banner2 = EmergencyBanner(
            id = "id2",
            title = "Title 2",
            body = "Body 2",
            link = Link("Link Title", "http://url2"),
            type = EmergencyBannerType.NATIONAL_EMERGENCY,
            allowsDismissal = false
        )

        coEvery { flagRepo.isLocalServicesEnabled() } returns true
        every { configRepo.emergencyBanners } returns listOf(banner1, banner2)
        every { appRepo.suppressedHomeWidgets } returns flowOf(setOf("id1"))

        val viewModel = AppViewModel(timeoutManager, appRepo, loginRepo, termsRepo, configRepo, flagRepo, authRepo, topicsFeature,
            localFeature, searchFeature, visited, chatFeature, analyticsClient, appNavigation)

        runTest {
            val homeWidgets = viewModel.homeWidgets.first { it != null }!!
            val bannerWidget1 = HomeWidget.Banner(banner1)
            val bannerWidget2 = HomeWidget.Banner(banner2)
            assertFalse("Suppressed banner (id1) should NOT be shown", homeWidgets.contains(bannerWidget1))
            assertTrue("Unsuppressed banner (id2) SHOULD be shown", homeWidgets.contains(bannerWidget2))
        }
    }
}
