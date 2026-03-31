package uk.gov.govuk.dvla.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import uk.gov.govuk.dvla.ui.DvlaLinkingRoute

const val DVLA_LINK_ROUTE = "dvla_link_route"
const val DVLA_DEEP_LINK_PATH = "/returnedToken"
const val ARG_DVLA_TOKEN = "token"

fun NavGraphBuilder.dvlaGraph(
    launchBrowser: (String) -> Unit,
    onLinkComplete: () -> Unit,
    onUnlinkComplete: () -> Unit,
    onAlertDismiss: () -> Unit,          
    modifier: Modifier = Modifier
) {
    composable(
        route = "$DVLA_LINK_ROUTE?$ARG_DVLA_TOKEN={$ARG_DVLA_TOKEN}",
        arguments = listOf(
            navArgument(ARG_DVLA_TOKEN) {
                type = NavType.StringType
                nullable = true
            }
        )
    ) {
        DvlaLinkingRoute(
            onLaunchBrowser = launchBrowser,
            onLinkComplete = onLinkComplete,
            onUnlinkComplete = onUnlinkComplete,
            onAlertDismiss = onAlertDismiss,
            modifier = modifier
        )
    }
}

fun NavController.navigateToDvlaLink() {
    navigate(DVLA_LINK_ROUTE)
}