package uk.gov.govuk.home.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import uk.gov.govuk.config.data.local.model.HomeWidget
import uk.gov.govuk.home.ui.HomeRoute

const val HOME_CONTAINER_ROUTE = "home_container_route" // wrapper for the Home tab to enable backstack state saving
const val HOME_GRAPH_ROUTE = "home_graph_route" // nested graph that handles the main home section
private const val HOME_ROUTE = "home_route"
const val HOME_GRAPH_START_DESTINATION = HOME_ROUTE

val homeDeepLinks = mapOf("/home" to listOf(HOME_ROUTE))

fun NavGraphBuilder.homeGraph(
    widgets: List<@Composable (Modifier) -> Unit>,
    homeWidgets: List<HomeWidget>?,
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
                homeWidgets = homeWidgets,
                modifier = modifier,
                headerWidget = headerWidget
            )
        }
    }
}
