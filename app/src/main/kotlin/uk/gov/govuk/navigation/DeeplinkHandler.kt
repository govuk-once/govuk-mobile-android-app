package uk.gov.govuk.navigation

import android.net.Uri
import androidx.navigation.NavController
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.config.data.flags.FlagRepo
import uk.gov.govuk.extension.getUrlParam
import uk.gov.govuk.home.navigation.HOME_GRAPH_ROUTE
import uk.gov.govuk.home.navigation.homeDeepLinks
import uk.gov.govuk.search.navigation.searchDeepLinks
import uk.gov.govuk.settings.navigation.settingsDeepLinks
import uk.gov.govuk.topics.navigation.topicsDeepLinks
import uk.gov.govuk.visited.navigation.visitedDeepLinks
import javax.inject.Inject

internal class DeeplinkHandler @Inject constructor(
    private val flagRepo: FlagRepo,
    private val analyticsClient: AnalyticsClient
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
                putAll(topicsDeepLinks)
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
}