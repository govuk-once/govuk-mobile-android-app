package uk.gov.govuk.dvla.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import uk.gov.govuk.dvla.ui.DvlaLinkingRoute

const val DVLA_LINK_ROUTE = "dvla_link_route"

fun NavGraphBuilder.dvlaGraph(
    launchBrowser: (String) -> Unit,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    composable(route = DVLA_LINK_ROUTE) {
        DvlaLinkingRoute(
            onLaunchBrowser = launchBrowser,
            onComplete = onComplete,
            modifier = modifier
        )
    }
}

fun NavController.navigateToDvlaLink() {
    navigate(DVLA_LINK_ROUTE)
}