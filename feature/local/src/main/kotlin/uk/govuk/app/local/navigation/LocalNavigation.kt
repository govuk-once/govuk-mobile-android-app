package uk.govuk.app.local.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import uk.govuk.app.local.ui.LocalAddressSelectRoute
import uk.govuk.app.local.ui.LocalAuthoritySelectRoute
import uk.govuk.app.local.ui.LocalConfirmationRoute
import uk.govuk.app.local.ui.LocalExplainerRoute
import uk.govuk.app.local.ui.LocalLookupRoute

const val LOCAL_GRAPH_ROUTE = "local_graph_route"
private const val LOCAL_EXPLAINER_ROUTE = "local_explainer_route"
const val LOCAL_LOOKUP_ROUTE = "local_lookup_route"
private const val LOCAL_AUTHORITY_SELECT_ROUTE = "local_authority_select_route"
private const val LOCAL_ADDRESS_SELECT_ROUTE = "local_address_select_route"
private const val LOCAL_CONFIRMATION_ROUTE = "local_confirmation_route"

fun NavGraphBuilder.localGraph(
    navController: NavHostController,
    onLocalAuthoritySelected: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    navigation(
        route = LOCAL_GRAPH_ROUTE,
        startDestination = LOCAL_EXPLAINER_ROUTE
    ) {
        composable(LOCAL_EXPLAINER_ROUTE) {
            LocalExplainerRoute(
                onBack = { navController.popBackStack() },
                onContinue = { navController.navigateToLocalEdit() },
                modifier = modifier,
            )
        }
        composable(LOCAL_LOOKUP_ROUTE) {
            LocalLookupRoute(
                onBack = { navController.popBackStack() },
                onCancel = onCancel,
                onLocalAuthoritySelected = { navController.navigate(LOCAL_CONFIRMATION_ROUTE) },
                onAddresses = { postcode ->
                    navController.navigateToLocalAuthoritySelect(postcode)
                },
                modifier = modifier
            )
        }
        composable(
            "$LOCAL_AUTHORITY_SELECT_ROUTE/{postcode}",
            arguments = listOf(
                navArgument("postcode") {
                    type = NavType.StringType
                },
            )
        ) { backStackEntry ->
                val postcode = backStackEntry.arguments?.getString("postcode") ?: ""
            LocalAuthoritySelectRoute(
                onBack = { navController.popBackStack() },
                onCancel = onCancel,
                onLocalAuthoritySelected = { navController.navigate(LOCAL_CONFIRMATION_ROUTE) },
                onSelectByAddress = { navController.navigateToLocalAddressSelect() },
                postcode = postcode,
                modifier = modifier
            )
        }
        composable(LOCAL_ADDRESS_SELECT_ROUTE) {
            LocalAddressSelectRoute(
                onBack = { navController.popBackStack() },
                onCancel = onCancel,
                onLocalAuthoritySelected = { navController.navigate(LOCAL_CONFIRMATION_ROUTE) },
                modifier = modifier
            )
        }
        composable(LOCAL_CONFIRMATION_ROUTE) {
            LocalConfirmationRoute(
                onBack = { navController.popBackStack() },
                onCancel = onCancel,
                onDone = onLocalAuthoritySelected,
                modifier = modifier
            )
        }
    }
}

fun NavController.navigateToLocalEdit() {
    navigate(LOCAL_LOOKUP_ROUTE)
}

fun NavController.navigateToLocalAuthoritySelect(postcode: String) {
    navigate("$LOCAL_AUTHORITY_SELECT_ROUTE/$postcode")
}

fun NavController.navigateToLocalAddressSelect() {
    navigate(LOCAL_ADDRESS_SELECT_ROUTE)
}
