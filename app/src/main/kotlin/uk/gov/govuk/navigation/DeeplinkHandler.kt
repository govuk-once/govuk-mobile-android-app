package uk.gov.govuk.navigation

import android.net.Uri
import androidx.navigation.NavController
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.chat.navigation.chatDeepLinks
import uk.gov.govuk.config.data.flags.FlagRepo
import uk.gov.govuk.data.identity.model.LinkedService
import uk.gov.govuk.dvla.navigation.ARG_DVLA_TOKEN
import uk.gov.govuk.dvla.navigation.DVLA_DEEP_LINK_PATH
import uk.gov.govuk.dvla.navigation.DVLA_LINK_ROUTE
import uk.gov.govuk.extension.getUrlParam
import uk.gov.govuk.home.navigation.HOME_GRAPH_ROUTE
import uk.gov.govuk.home.navigation.homeDeepLinks
import uk.gov.govuk.search.navigation.searchDeepLinks
import uk.gov.govuk.settings.navigation.settingsDeepLinks
import uk.gov.govuk.topics.navigation.TopicsDeepLinksProvider
import uk.gov.govuk.visited.navigation.visitedDeepLinks
import javax.inject.Inject

internal class DeeplinkHandler @Inject constructor(
    private val flagRepo: FlagRepo,
    private val analyticsClient: AnalyticsClient,
    private val topicsDeepLinksProvider: TopicsDeepLinksProvider
) {

    companion object {
        private const val LINKED_SERVICE_CALLBACK_PREFIX = "callback"
        private const val LINKED_SERVICE_AUTH_SUFFIX = "auth"
        private const val LINKED_SERVICE_SEGMENT_COUNT = 3
    }

    var deepLink: Uri? = null

    private val deepLinks: Map<String, List<String>> by lazy {
        buildMap {
            putAll(homeDeepLinks)
            putAll(settingsDeepLinks)

            if (flagRepo.isChatEnabled()) {
                putAll(chatDeepLinks)
            }

            if (flagRepo.isSearchEnabled()) {
                putAll(searchDeepLinks)
            }

            if (flagRepo.isTopicsEnabled()) {
                putAll(topicsDeepLinksProvider.deepLinks)
            }

            if (flagRepo.isRecentActivityEnabled()) {
                putAll(visitedDeepLinks)
            }
        }
    }

    var onLaunchBrowser: ((String) -> Unit)? = null
    var onDeeplinkNotFound: (() -> Unit)? = null

    fun handleDeeplink(navController: NavController) {
        deepLink?.let {

            // check for intercepted route first
            if (interceptLinkedServiceCallback(it, navController)) {
                deepLink = null
                return
            }

            var validDeeplink = true

            deepLinks[it.path]?.let { routes ->
                navController.navigate(HOME_GRAPH_ROUTE) {
                    popUpTo(0) { inclusive = true }
                }

                // Construct backstack and navigate to deeplink route
                for (route in routes) {
                    navController.navigate(route) {
                        launchSingleTop = true
                    }
                }
            } ?: run {
                it.getUrlParam(DeepLink.allowedGovUkUrls)?.let { uri ->
                    onLaunchBrowser?.invoke(uri.toString())
                } ?: run {
                    validDeeplink = false
                    onDeeplinkNotFound?.invoke()
                }
            }        // prevent accidentally intercepting longer paths


            analyticsClient.deepLinkEvent(validDeeplink, it.toString())
            deepLink = null
        }
    }

    /** Intercepts /callback/{service}/auth routes */
    private fun interceptLinkedServiceCallback(uri: Uri, navController: NavController): Boolean {
        val segments = uri.pathSegments

        // prevent accidentally handling longer paths
        if (segments.size != LINKED_SERVICE_SEGMENT_COUNT) return false

        val (prefix, serviceName, suffix) = segments
        if (prefix != LINKED_SERVICE_CALLBACK_PREFIX || suffix != LINKED_SERVICE_AUTH_SUFFIX) return false

        val service = LinkedService.entries.find { it.serviceName == serviceName }

        return when (service) {
            LinkedService.DVLA -> handleDvlaCallback(uri, navController)
            // add any future linked service callbacks here
            null -> false
        }
    }


    /** Intercepts the DVLA auth callback */
    private fun handleDvlaCallback(uri: Uri, navController: NavController): Boolean {
        if (uri.path != DVLA_DEEP_LINK_PATH) return false

        val token = uri.getQueryParameter(ARG_DVLA_TOKEN)

        // TODO Awaiting clarification from DVLA, success and 'do nothing' cases only are handled at the moment
        if (!token.isNullOrBlank()) {
            navController.navigate("$DVLA_LINK_ROUTE?$ARG_DVLA_TOKEN=$token") {
                popUpTo(DVLA_LINK_ROUTE) { inclusive = true }
                launchSingleTop = true
            }
        }
        // error messages are shown in the web flow and the user is taken back to the app

        return true
    }
}
