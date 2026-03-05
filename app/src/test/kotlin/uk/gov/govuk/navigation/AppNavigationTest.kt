package uk.gov.govuk.navigation

import android.net.Uri
import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.analytics.navigation.ANALYTICS_GRAPH_ROUTE
import uk.gov.govuk.data.auth.AuthRepo
import uk.gov.govuk.home.navigation.HOME_GRAPH_ROUTE
import uk.gov.govuk.login.navigation.LOGIN_GRAPH_ROUTE
import uk.gov.govuk.notifications.navigation.NOTIFICATIONS_CONSENT_ON_NEXT_ROUTE
import uk.gov.govuk.notifications.navigation.NOTIFICATIONS_CONSENT_ROUTE
import uk.gov.govuk.notifications.navigation.NOTIFICATIONS_ONBOARDING_GRAPH_ROUTE
import uk.gov.govuk.terms.navigation.TERMS_GRAPH_ROUTE
import uk.gov.govuk.topics.navigation.TOPIC_SELECTION_GRAPH_ROUTE

class AppNavigationTest {

    private val authRepo = mockk<AuthRepo>(relaxed = true)
    private val deeplinkHandler = mockk<DeeplinkHandler>(relaxed = true)
    private val navController = mockk<NavController>(relaxed = true)
    private val deeplink = mockk<Uri>(relaxed = true)

    private lateinit var appLaunchNav: AppNavigation

    @Before
    fun setup() {
        appLaunchNav = AppNavigation(
            authRepo,
            deeplinkHandler
        )
    }

    @Test
    fun `Set launch browser on deeplink handler`() {
        val onLaunchBrowser: (String) -> Unit = { }
        appLaunchNav.setOnLaunchBrowser(onLaunchBrowser)
        verify {
            deeplinkHandler.onLaunchBrowser = onLaunchBrowser
        }
    }

    @Test
    fun `Set deeplink not found on deeplink handler`() {
        val onDeeplinkNotFound: () -> Unit = { }
        appLaunchNav.setOnDeeplinkNotFound(onDeeplinkNotFound)
        verify {
            deeplinkHandler.onDeeplinkNotFound = onDeeplinkNotFound
        }
    }

    @Test
    fun `Set deeplink when user session not active`() {
        every { authRepo.isUserSessionActive() } returns false
        appLaunchNav.setDeeplink(navController, deeplink)

        verify {
            deeplinkHandler.deepLink = deeplink
        }

        verify(exactly = 0) {
            deeplinkHandler.handleDeeplink(any())
        }
    }

    @Test
    fun `Set deeplink when user session active`() {
        every { authRepo.isUserSessionActive() } returns true
        appLaunchNav.setDeeplink(navController, deeplink)

        verify {
            deeplinkHandler.deepLink = deeplink
            deeplinkHandler.handleDeeplink(navController)
        }
    }

    @Test
    fun `On sign out navigates to login`() {
        appLaunchNav.onSignOut(navController)

        verify {
            navController.navigate(LOGIN_GRAPH_ROUTE, any<NavOptionsBuilder.() -> Unit>())
        }
    }

    @Test
    fun `NavigateToTerms pops backstack and navigates`() {
        appLaunchNav.navigateToTerms(navController)
        verify {
            navController.popBackStack()
            navController.navigate(TERMS_GRAPH_ROUTE)
        }
    }

    @Test
    fun `NavigateToAnalytics pops backstack and navigates`() {
        appLaunchNav.navigateToAnalytics(navController)
        verify {
            navController.popBackStack()
            navController.navigate(ANALYTICS_GRAPH_ROUTE)
        }
    }

    @Test
    fun `NavigateToTopicSelection pops backstack and navigates`() {
        appLaunchNav.navigateToTopicSelection(navController)
        verify {
            navController.popBackStack()
            navController.navigate(TOPIC_SELECTION_GRAPH_ROUTE)
        }
    }

    @Test
    fun `NavigateToNotificationsOnboarding pops backstack and navigates`() {
        appLaunchNav.navigateToNotificationsOnboarding(navController)
        verify {
            navController.popBackStack()
            navController.navigate(NOTIFICATIONS_ONBOARDING_GRAPH_ROUTE)
        }
    }

    @Test
    fun `NavigateToNotificationsConsentOnNext pops backstack and navigates`() {
        appLaunchNav.navigateToNotificationsConsentOnNext(navController)
        verify {
            navController.popBackStack()
            navController.navigate(NOTIFICATIONS_CONSENT_ON_NEXT_ROUTE)
        }
    }

    @Test
    fun `NavigateToHome pops backstack, navigates, and handles deeplink`() {
        appLaunchNav.navigateToHome(navController)
        verify {
            navController.popBackStack()
            navController.navigate(HOME_GRAPH_ROUTE)
            deeplinkHandler.handleDeeplink(navController)
        }
    }

    @Test
    fun `NavigateToLogin navigates with single top`() {
        appLaunchNav.navigateToLogin(navController)
        verify {
            navController.navigate(LOGIN_GRAPH_ROUTE, any<NavOptionsBuilder.() -> Unit>())
        }
    }

    @Test
    fun `NavigateToNotificationsConsent navigates directly`() {
        appLaunchNav.navigateToNotificationsConsent(navController)
        verify {
            navController.navigate(NOTIFICATIONS_CONSENT_ROUTE)
        }
    }

    @Test
    fun `On sign out navigates to login with pop up to`() {
        appLaunchNav.onSignOut(navController)
        verify {
            navController.navigate(LOGIN_GRAPH_ROUTE, any<NavOptionsBuilder.() -> Unit>())
        }
    }
}
