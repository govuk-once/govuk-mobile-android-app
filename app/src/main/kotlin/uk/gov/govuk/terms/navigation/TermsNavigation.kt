package uk.gov.govuk.terms.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import uk.gov.govuk.terms.ui.TermsRoute

const val TERMS_GRAPH_ROUTE = "terms_graph_route"
private const val TERMS_ROUTE = "terms_route"

fun NavGraphBuilder.termsGraph(
    onCompleted: () -> Unit,
    launchBrowser: (url: String) -> Unit,
    modifier: Modifier = Modifier
) {
    navigation(
        route = TERMS_GRAPH_ROUTE,
        startDestination = TERMS_ROUTE
    ) {
        composable(route = TERMS_ROUTE) {
            TermsRoute(
                onCompleted = onCompleted,
                launchBrowser = launchBrowser,
                modifier = modifier
            )
        }
    }
}