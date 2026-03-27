package uk.gov.govuk.navigation

import android.net.Uri
import androidx.navigation.NavController
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.config.data.flags.FlagRepo
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
    var deepLink: Uri? = null

    private val deepLinks: Map<String, List<String>> by lazy {
        buildMap {
            putAll(homeDeepLinks)
            putAll(settingsDeepLinks)

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
            if (interceptDvlaAuthCallback(it, navController)) {
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
            }

            analyticsClient.deepLinkEvent(validDeeplink, it.toString())
            deepLink = null
        }
    }

    /** Intercepts the DVLA auth callback */
    private fun interceptDvlaAuthCallback(uri: Uri, navController: NavController): Boolean {
        if (uri.path != DVLA_DEEP_LINK_PATH) return false

        uri.getQueryParameter(ARG_DVLA_TOKEN)?.let { token ->
            navController.navigate("$DVLA_LINK_ROUTE?$ARG_DVLA_TOKEN=$token") {
                popUpTo(DVLA_LINK_ROUTE) { inclusive = true }
                launchSingleTop = true
            }
            // TODO: do we need analytics here? (if yes, on error too?)
        } ?: run {
            // TODO: Handle auth failure (missing token etc) when we get requirements
        }

        return true
    }
}