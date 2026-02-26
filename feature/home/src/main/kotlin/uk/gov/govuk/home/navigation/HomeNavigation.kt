package uk.gov.govuk.home.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import uk.gov.govuk.home.ui.HomeRoute

const val HOME_GRAPH_ROUTE = "home_graph_route"
private const val HOME_ROUTE = "home_route"
const val HOME_GRAPH_START_DESTINATION = HOME_ROUTE

val homeDeepLinks = mapOf("/home" to listOf(HOME_ROUTE))

fun NavGraphBuilder.homeGraph(
    widgets: List<@Composable (Modifier) -> Unit>,
    modifier: Modifier = Modifier,
    headerWidget: (@Composable (Modifier) -> Unit)? = null,
    transitionOverrideRoutes: List<String> = emptyList()
) {
    navigation(
        route = HOME_GRAPH_ROUTE,
        startDestination = HOME_GRAPH_START_DESTINATION
    ) {
        composable(
            HOME_ROUTE,
            exitTransition = {
                if (transitionOverrideRoutes.contains(this.targetState.destination.parent?.route)) {
                    ExitTransition.None
                } else {
                    null
                }
            },
            popEnterTransition = {
                if (transitionOverrideRoutes.contains(this.initialState.destination.parent?.route)) {
                    EnterTransition.None
                } else {
                    null
                }
            },
        ) {
            HomeRoute(
                widgets = widgets,
                modifier = modifier,
                headerWidget = headerWidget
            )
        }
    }
}
