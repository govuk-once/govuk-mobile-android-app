package uk.gov.govuk.search.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import uk.gov.govuk.search.ui.SearchRoute

const val SEARCH_GRAPH_ROUTE = "search_graph_route"
const val SEARCH_ROUTE = "search_route"

val searchDeepLinks = mapOf("/search" to listOf(SEARCH_ROUTE))

fun NavGraphBuilder.searchGraph(
    navController: NavHostController,
    launchBrowser: (url: String) -> Unit,
    modifier: Modifier = Modifier
) {
    navigation(
        route = SEARCH_GRAPH_ROUTE,
        startDestination = SEARCH_ROUTE
    ) {
        composable(
            SEARCH_ROUTE,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None }
        ) {
            SearchRoute(
                onBack = { navController.popBackStack() },
                launchBrowser = launchBrowser,
                modifier = modifier
            )
        }
    }
}
