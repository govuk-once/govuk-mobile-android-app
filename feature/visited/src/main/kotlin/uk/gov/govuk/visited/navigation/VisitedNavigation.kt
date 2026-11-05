package uk.gov.govuk.visited.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import uk.gov.govuk.visited.ui.VisitedRoute

const val VISITED_GRAPH_ROUTE = "visited_graph_route"
const val VISITED_ROUTE = "visited_route"

val visitedDeepLinks = mapOf(
    "/visited" to listOf(VISITED_ROUTE)
)

fun NavGraphBuilder.visitedGraph(
    navController: NavHostController,
    launchBrowser: (url: String) -> Unit,
    modifier: Modifier = Modifier
) {
    navigation(
        route = VISITED_GRAPH_ROUTE,
        startDestination = VISITED_ROUTE
    ) {
        composable(VISITED_ROUTE) {
            VisitedRoute(
                onBack = { navController.popBackStack() },
                launchBrowser = launchBrowser,
                modifier = modifier
            )
        }
    }
}
