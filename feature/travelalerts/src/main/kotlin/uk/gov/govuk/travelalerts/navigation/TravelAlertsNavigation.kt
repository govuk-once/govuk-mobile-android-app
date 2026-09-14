package uk.gov.govuk.travelalerts.navigation

import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import uk.gov.govuk.travelalerts.ui.countrylist.CountryListScreen

const val COUNTRY_LIST_ROUTE = "country_list_route"

fun NavGraphBuilder.travelAlertsGraph(
    navController: NavController,
    launchBrowser: (url: String) -> Unit,
    modifier: Modifier
) {
    composable(
        route = COUNTRY_LIST_ROUTE,
        enterTransition = { slideInVertically { it } },
        popExitTransition = { slideOutVertically { it } }
    ) {
        CountryListScreen(
            onClose = { navController.popBackStack() },
            modifier = modifier
        )
    }
}
