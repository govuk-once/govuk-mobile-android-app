package uk.gov.govuk.dwp.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import uk.gov.govuk.dwp.ui.CurrentPaymentsRoute
import uk.gov.govuk.dwp.ui.DwpLinkingRoute
import uk.gov.govuk.dwp.ui.HistoricalPaymentsRoute

const val DWP_GRAPH_ROUTE = "dwp_graph_route"
internal const val DWP_LINK_ROUTE = "dwp_link_route"
internal const val CURRENT_PAYMENTS_ROUTE = "current_payments_route"
internal const val HISTORICAL_PAYMENTS_ROUTE = "historical_payments_route/{benefitCode}/{benefitName}/{frequency}"

fun NavGraphBuilder.dwpGraph(
    navController: NavController,
    onClose: () -> Unit,
    onLinkComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    navigation(route = DWP_GRAPH_ROUTE, startDestination = DWP_LINK_ROUTE) {
        composable(DWP_LINK_ROUTE) {
            DwpLinkingRoute(onClose = onClose, onLinkComplete = onLinkComplete, modifier = modifier)
        }
        composable(CURRENT_PAYMENTS_ROUTE) {
            CurrentPaymentsRoute(
                onClick = { benefitCode, benefitName, frequency ->
                    navController.navigateToHistoricalPayments(benefitCode, benefitName, frequency)
                },
                modifier = modifier
            )
        }
        composable(
            route = HISTORICAL_PAYMENTS_ROUTE,
            arguments = listOf(
                navArgument("benefitCode") { type = NavType.StringType },
                navArgument("benefitName") { type = NavType.StringType },
                navArgument("frequency") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val benefitCode = backStackEntry.arguments?.getString("benefitCode").orEmpty()
            val benefitName = backStackEntry.arguments?.getString("benefitName").orEmpty()
            val frequency = backStackEntry.arguments?.getString("frequency").orEmpty()

            HistoricalPaymentsRoute(
                benefitCode = benefitCode,
                benefitName = benefitName,
                frequency = frequency,
                modifier = modifier
            )
        }
    }
}

fun NavController.navigateToDwpLinkIntro() {
    navigate(DWP_GRAPH_ROUTE) { launchSingleTop = true }
}

fun NavController.navigateToHistoricalPayments(
    benefitCode: String,
    benefitName: String,
    frequency: String
) {
    navigate("historical_payments_route/$benefitCode/$benefitName/$frequency")
}
