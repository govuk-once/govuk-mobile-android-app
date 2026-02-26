package uk.gov.govuk.navigation

import android.net.Uri
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.config.data.flags.FlagRepo
import uk.gov.govuk.home.navigation.HOME_GRAPH_ROUTE
import uk.gov.govuk.search.navigation.SEARCH_ROUTE
import uk.gov.govuk.topics.navigation.TOPICS_EDIT_ROUTE
import uk.gov.govuk.visited.navigation.VISITED_ROUTE

class DeeplinkHandlerTest {

    private val flagRepo = mockk<FlagRepo>(relaxed = true)
    private val analyticsClient = mockk<AnalyticsClient>(relaxed = true)
    private val navController = mockk<NavController>(relaxed = true)
    private val onLaunchBrowser = mockk<((String) -> Unit)>(relaxed = true)
    private val onDeeplinkNotFound = mockk<(() -> Unit)>(relaxed = true)
    private val deeplink = mockk<Uri>(relaxed = true)
    private val urlParam = mockk<Uri>(relaxed = true)

    private lateinit var deeplinkHandler: DeeplinkHandler

    @Before
    fun setup() {
        mockkStatic(Uri::class)

        deeplinkHandler = DeeplinkHandler(flagRepo, analyticsClient)
        deeplinkHandler.onLaunchBrowser = onLaunchBrowser
        deeplinkHandler.onDeeplinkNotFound = onDeeplinkNotFound
        deeplinkHandler.deepLink = deeplink
    }

    @Test
    fun `Handle null deeplink`() {
        deeplinkHandler.deepLink = null

        deeplinkHandler.handleDeeplink(navController)

        verify(exactly = 0) {
            navController.navigate(any(), any<NavOptionsBuilder.() -> Unit>())
            analyticsClient.deepLinkEvent(any(), any())
            onLaunchBrowser.invoke(any())
            onDeeplinkNotFound.invoke()
        }
    }

    @Test
    fun `Reset deeplink after handling`() {
        every { deeplink.path } returns "/home"
        every { deeplink.toString() } returns "govuk://gov.uk/home"

        deeplinkHandler.handleDeeplink(navController)

        clearAllMocks()

        deeplinkHandler.handleDeeplink(navController)

        verify(exactly = 0) {
            navController.navigate(any(), any<NavOptionsBuilder.() -> Unit>())
            analyticsClient.deepLinkEvent(any(), any())
            onLaunchBrowser.invoke(any())
            onDeeplinkNotFound.invoke()
        }
    }

    @Test
    fun `Handle home deeplink`() {
        every { deeplink.path } returns "/home"
        every { deeplink.toString() } returns "govuk://gov.uk/home"

        deeplinkHandler.deepLink = deeplink

        deeplinkHandler.handleDeeplink(navController)

        verify {
            navController.navigate(HOME_GRAPH_ROUTE, any<NavOptionsBuilder.() -> Unit>())
            analyticsClient.deepLinkEvent(true, "govuk://gov.uk/home")
        }
    }

    @Test
    fun `Handle settings deeplink`() {
        every { deeplink.path } returns "/settings"
        every { deeplink.toString() } returns "govuk://gov.uk/settings"

        deeplinkHandler.deepLink = deeplink

        deeplinkHandler.handleDeeplink(navController)

        verify {
            navController.navigate(HOME_GRAPH_ROUTE, any<NavOptionsBuilder.() -> Unit>())
            analyticsClient.deepLinkEvent(true, "govuk://gov.uk/settings")
        }
    }

    @Test
    fun `Handle search deeplink`() {
        every { flagRepo.isSearchEnabled() } returns true
        every { deeplink.path } returns "/search"
        every { deeplink.toString() } returns "govuk://gov.uk/search"

        deeplinkHandler.deepLink = deeplink

        deeplinkHandler.handleDeeplink(navController)

        verify {
            navController.navigate(HOME_GRAPH_ROUTE, any<NavOptionsBuilder.() -> Unit>())
            navController.navigate(SEARCH_ROUTE, any<NavOptionsBuilder.() -> Unit>())
            analyticsClient.deepLinkEvent(true, "govuk://gov.uk/search")
        }
    }

    @Test
    fun `Handle search deeplink when search is disabled`() {
        every { flagRepo.isSearchEnabled() } returns false
        every { deeplink.path } returns "/search"
        every { deeplink.toString() } returns "govuk://gov.uk/search"
        every { deeplink.getQueryParameter(any()) } returns null

        deeplinkHandler.handleDeeplink(navController)

        verify {
            analyticsClient.deepLinkEvent(false, "govuk://gov.uk/search")
            onDeeplinkNotFound.invoke()
        }

        verify(exactly = 0) {
            navController.navigate(any(), any<NavOptionsBuilder.() -> Unit>())
            onLaunchBrowser.invoke(any())
        }
    }

    @Test
    fun `Handle topics edit deeplink`() {
        every { flagRepo.isTopicsEnabled() } returns true
        every { deeplink.path } returns "/topics/edit"
        every { deeplink.toString() } returns "govuk://gov.uk/topics/edit"

        deeplinkHandler.deepLink = deeplink

        deeplinkHandler.handleDeeplink(navController)

        verify {
            navController.navigate(HOME_GRAPH_ROUTE, any<NavOptionsBuilder.() -> Unit>())
            navController.navigate(TOPICS_EDIT_ROUTE, any<NavOptionsBuilder.() -> Unit>())
            analyticsClient.deepLinkEvent(true, "govuk://gov.uk/topics/edit")
        }
    }

    @Test
    fun `Handle topics deeplink when topics is disabled`() {
        every { flagRepo.isTopicsEnabled() } returns false
        every { deeplink.path } returns "/topics/edit"
        every { deeplink.toString() } returns "govuk://gov.uk/topics/edit"
        every { deeplink.getQueryParameter(any()) } returns null

        deeplinkHandler.handleDeeplink(navController)

        verify {
            analyticsClient.deepLinkEvent(false, "govuk://gov.uk/topics/edit")
            onDeeplinkNotFound.invoke()
        }

        verify(exactly = 0) {
            navController.navigate(any(), any<NavOptionsBuilder.() -> Unit>())
            onLaunchBrowser.invoke(any())
        }
    }

    @Test
    fun `Handle visited deeplink`() {
        every { flagRepo.isRecentActivityEnabled() } returns true
        every { deeplink.path } returns "/visited"
        every { deeplink.toString() } returns "govuk://gov.uk/visited"

        deeplinkHandler.deepLink = deeplink

        deeplinkHandler.handleDeeplink(navController)

        verify {
            navController.navigate(HOME_GRAPH_ROUTE, any<NavOptionsBuilder.() -> Unit>())
            navController.navigate(VISITED_ROUTE, any<NavOptionsBuilder.() -> Unit>())
            analyticsClient.deepLinkEvent(true, "govuk://gov.uk/visited")
        }
    }

    @Test
    fun `Handle visited deeplink when recent activity is disabled`() {
        every { flagRepo.isRecentActivityEnabled() } returns false
        every { deeplink.path } returns "/visted"
        every { deeplink.toString() } returns "govuk://gov.uk/visited"
        every { deeplink.getQueryParameter(any()) } returns null

        deeplinkHandler.handleDeeplink(navController)

        verify {
            analyticsClient.deepLinkEvent(false, "govuk://gov.uk/visited")
            onDeeplinkNotFound.invoke()
        }

        verify(exactly = 0) {
            navController.navigate(any(), any<NavOptionsBuilder.() -> Unit>())
            onLaunchBrowser.invoke(any())
        }
    }

    @Test
    fun `Handle deeplink to web url`() {
        every { deeplink.getQueryParameter("url")?.toUri() } returns urlParam
        every { urlParam.scheme } returns "https"
        every { urlParam.host } returns "www.gov.uk"
        every { deeplink.toString() } returns "govuk://gov.uk?url=https://www.gov.uk/page"

        deeplinkHandler.handleDeeplink(navController)

        verify {
            analyticsClient.deepLinkEvent(true, "govuk://gov.uk?url=https://www.gov.uk/page")
            onLaunchBrowser.invoke(any())
        }

        verify(exactly = 0) {
            navController.navigate(any(), any<NavOptionsBuilder.() -> Unit>())
            onDeeplinkNotFound.invoke()
        }
    }

    @Test
    fun `Handle broken deeplink`() {
        every { deeplink.path } returns "/blah"
        every { deeplink.toString() } returns "govuk://gov.uk/blah"
        every { deeplink.getQueryParameter(any()) } returns null

        deeplinkHandler.handleDeeplink(navController)

        verify {
            analyticsClient.deepLinkEvent(false, "govuk://gov.uk/blah")
            onDeeplinkNotFound.invoke()
        }

        verify(exactly = 0) {
            navController.navigate(any(), any<NavOptionsBuilder.() -> Unit>())
            onLaunchBrowser.invoke(any())
        }
    }
}