package uk.gov.govuk.dwp.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import uk.gov.govuk.dwp.ui.DwpLinkingRoute

const val DWP_GRAPH_ROUTE = "dwp_graph_route"
internal const val DWP_LINK_ROUTE = "dwp_link_route"

fun NavGraphBuilder.dwpGraph(
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    navigation(route = DWP_GRAPH_ROUTE, startDestination = DWP_LINK_ROUTE) {
        composable(DWP_LINK_ROUTE) {
            DwpLinkingRoute(onClose = onClose, modifier = modifier)
        }
    }
}

fun NavController.navigateToDwpLinkIntro() {
    navigate(DWP_GRAPH_ROUTE) { launchSingleTop = true }
}
