package uk.gov.govuk.navigation

import android.net.Uri
import androidx.navigation.NavController
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.analytics.navigation.ANALYTICS_GRAPH_ROUTE
import uk.gov.govuk.config.data.flags.FlagRepo
import uk.gov.govuk.data.AppRepo
import uk.gov.govuk.data.auth.AuthRepo
import uk.gov.govuk.home.navigation.HOME_GRAPH_ROUTE
import uk.gov.govuk.login.navigation.LOGIN_GRAPH_ROUTE
import uk.gov.govuk.notifications.data.NotificationsRepo
import uk.gov.govuk.notifications.navigation.NOTIFICATIONS_CONSENT_ON_NEXT_ROUTE
import uk.gov.govuk.notifications.navigation.NOTIFICATIONS_CONSENT_ROUTE
import uk.gov.govuk.notifications.navigation.NOTIFICATIONS_ONBOARDING_GRAPH_ROUTE
import uk.gov.govuk.topics.TopicsFeature
import uk.gov.govuk.topics.navigation.TOPIC_SELECTION_GRAPH_ROUTE
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class AppNavigation @Inject constructor(
    private val flagRepo: FlagRepo,
    private val analyticsClient: AnalyticsClient,
    private val appRepo: AppRepo,
    private val authRepo: AuthRepo,
    private val topicsFeature: TopicsFeature,
    private val deeplinkHandler: DeeplinkHandler,
    private val notificationsRepo: NotificationsRepo
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

    suspend fun onNext(navController: NavController) {
        when {
            analyticsClient.isAnalyticsConsentRequired() -> navigate(navController, ANALYTICS_GRAPH_ROUTE)
            flagRepo.isTopicsEnabled() &&
                    !appRepo.isTopicSelectionCompleted() &&
                    topicsFeature.hasTopics() -> navigate(navController, TOPIC_SELECTION_GRAPH_ROUTE)
            flagRepo.isNotificationsEnabled() &&
                    !notificationsRepo.isNotificationsOnboardingCompleted() ->
                navigate(navController, NOTIFICATIONS_ONBOARDING_GRAPH_ROUTE)
            flagRepo.isNotificationsEnabled() &&
                    notificationsRepo.isNotificationsOnboardingCompleted() &&
                    notificationsRepo.permissionGranted() &&
                    !notificationsRepo.consentGiven() ->
                navigate(navController, NOTIFICATIONS_CONSENT_ON_NEXT_ROUTE)
            else -> {
                navigate(navController, HOME_GRAPH_ROUTE)
                deeplinkHandler.handleDeeplink(navController)
            }
        }
    }

    suspend fun onNotificationsOnboardingCompleted(navController: NavController) {
        notificationsRepo.notificationsOnboardingCompleted()
        onNext(navController)
    }

    suspend fun navigateOnResume(navController: NavController) {
        if (!authRepo.isUserSessionActive()) {
            navController.navigate(LOGIN_GRAPH_ROUTE) {
                launchSingleTop = true
            }
        }
        if (flagRepo.isNotificationsEnabled()) {
            navigateNotificationsOnResume(navController)
        }
    }

    private suspend fun navigateNotificationsOnResume(navController: NavController) {
        if (!notificationsRepo.permissionGranted()) {
            // TODO: awaiting failure requirements for sendRemoveConsent()
            notificationsRepo.sendRemoveConsent()
            notificationsRepo.removeConsent()
            if (navController.currentDestination?.route == NOTIFICATIONS_CONSENT_ON_NEXT_ROUTE) {
                onNext(navController)
            }
        } else if (
            authRepo.isUserSessionActive() &&
            notificationsRepo.isNotificationsOnboardingCompleted() &&
            !notificationsRepo.consentGiven()
        ) {
            navController.navigate(NOTIFICATIONS_CONSENT_ROUTE)
        }
    }

    private fun navigate(navController: NavController, route: String) {
        navController.popBackStack()
        navController.navigate(route)
    }

    fun onSignOut(navController: NavController) {
        notificationsRepo.logout()
        navController.navigate(LOGIN_GRAPH_ROUTE) {
            popUpTo(0) { inclusive = true }
        }
    }
}
