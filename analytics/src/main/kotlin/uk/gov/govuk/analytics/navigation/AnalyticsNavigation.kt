package uk.gov.govuk.analytics.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import uk.gov.govuk.analytics.ui.AnalyticsConsentRoute

const val ANALYTICS_GRAPH_ROUTE = "analytics_graph_route"
private const val ANALYTICS_CONSENT_ROUTE = "analytics_consent_route"

fun NavGraphBuilder.analyticsGraph(
    analyticsConsentCompleted: () -> Unit,
    launchBrowser: (url: String) -> Unit,
    modifier: Modifier = Modifier
) {
    navigation(
        route = ANALYTICS_GRAPH_ROUTE,
        startDestination = ANALYTICS_CONSENT_ROUTE
    ) {
        composable(ANALYTICS_CONSENT_ROUTE) {
            AnalyticsConsentRoute(
                analyticsConsentCompleted = analyticsConsentCompleted,
                launchBrowser = launchBrowser,
                modifier = modifier
            )
        }
    }
}
