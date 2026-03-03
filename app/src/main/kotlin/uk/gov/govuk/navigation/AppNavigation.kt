package uk.gov.govuk.navigation

import android.net.Uri
import androidx.navigation.NavController
import uk.gov.govuk.analytics.navigation.ANALYTICS_GRAPH_ROUTE
import uk.gov.govuk.data.auth.AuthRepo
import uk.gov.govuk.home.navigation.HOME_GRAPH_ROUTE
import uk.gov.govuk.login.navigation.LOGIN_GRAPH_ROUTE
import uk.gov.govuk.notifications.navigation.NOTIFICATIONS_CONSENT_ON_NEXT_ROUTE
import uk.gov.govuk.notifications.navigation.NOTIFICATIONS_CONSENT_ROUTE
import uk.gov.govuk.notifications.navigation.NOTIFICATIONS_ONBOARDING_GRAPH_ROUTE
import uk.gov.govuk.terms.navigation.TERMS_GRAPH_ROUTE
import uk.gov.govuk.topics.navigation.TOPIC_SELECTION_GRAPH_ROUTE
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class AppNavigation @Inject constructor(
    private val authRepo: AuthRepo,
    private val deeplinkHandler: DeeplinkHandler,
) {
    fun setOnLaunchBrowser(onLaunchBrowser: (String) -> Unit) {
        deeplinkHandler.onLaunchBrowser = onLaunchBrowser
    }

    fun setOnDeeplinkNotFound(onDeeplinkNotFound: () -> Unit) {
        deeplinkHandler.onDeeplinkNotFound = onDeeplinkNotFound
    }

    fun setDeeplink(navController: NavController, uri: Uri?) {
        deeplinkHandler.deepLink = uri
        if (authRepo.isUserSessionActive()) {
            deeplinkHandler.handleDeeplink(navController)
        }
    }

    fun navigateToTerms(navController: NavController) {
        navigate(navController, TERMS_GRAPH_ROUTE)
    }

    fun navigateToAnalytics(navController: NavController) {
        navigate(navController, ANALYTICS_GRAPH_ROUTE)
    }

    fun navigateToTopicSelection(navController: NavController) {
        navigate(navController, TOPIC_SELECTION_GRAPH_ROUTE)
    }

    fun navigateToNotificationsOnboarding(navController: NavController) {
        navigate(navController, NOTIFICATIONS_ONBOARDING_GRAPH_ROUTE)
    }

    fun navigateToNotificationsConsentOnNext(navController: NavController) {
        navigate(navController, NOTIFICATIONS_CONSENT_ON_NEXT_ROUTE)
    }

    fun navigateToHome(navController: NavController) {
        navigate(navController, HOME_GRAPH_ROUTE)
        deeplinkHandler.handleDeeplink(navController)
    }

    fun navigateToLogin(navController: NavController) {
        navController.navigate(LOGIN_GRAPH_ROUTE) {
            launchSingleTop = true
        }
    }

    fun navigateToNotificationsConsent(navController: NavController) {
        navController.navigate(NOTIFICATIONS_CONSENT_ROUTE)
    }

    private fun navigate(navController: NavController, route: String) {
        navController.popBackStack()
        navController.navigate(route)
    }

    fun onSignOut(navController: NavController) {
        navController.navigate(LOGIN_GRAPH_ROUTE) {
            popUpTo(0) { inclusive = true }
        }
    }
}
