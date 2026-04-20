package uk.gov.govuk.dvla.navigation

import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import uk.gov.govuk.dvla.DvlaViewModel
import uk.gov.govuk.dvla.ui.DvlaLinkIntroScreen
import uk.gov.govuk.dvla.ui.DvlaLinkingRoute

const val DVLA_GRAPH_ROUTE = "dvla_graph_route"
const val DVLA_LINK_INTRO_ROUTE = "dvla_link_intro_route"
const val DVLA_LINK_ROUTE = "dvla_link_route"
const val DVLA_DEEP_LINK_PATH = "/returnedToken"
const val ARG_DVLA_TOKEN = "token"

fun NavGraphBuilder.dvlaGraph(
    onContinueToLink: () -> Unit,
    launchBrowser: (String) -> Unit,
    onLinkComplete: () -> Unit,
    onUnlinkComplete: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    navigation(
        route = DVLA_GRAPH_ROUTE,
        startDestination = DVLA_LINK_INTRO_ROUTE
    ) {
        composable(route = DVLA_LINK_INTRO_ROUTE) {
            val viewModel: DvlaViewModel = hiltViewModel()

            DvlaLinkIntroScreen(
                onPageView = {
                    viewModel.onIntroPageView()
                },
                onClose = {
                    viewModel.onIntroCloseClicked()
                    onClose()
                },
                onContinue = { buttonText ->
                    viewModel.onIntroContinueClicked(buttonText)
                    onContinueToLink()
                },
                modifier = modifier
            )
        }

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
                onClose = onClose,
                modifier = modifier
            )
        }
    }
}

fun NavController.navigateToDvlaLinkIntro() {
    navigate(DVLA_LINK_INTRO_ROUTE)
}

fun NavController.navigateToDvlaLink() {
    navigate(DVLA_LINK_ROUTE)
}