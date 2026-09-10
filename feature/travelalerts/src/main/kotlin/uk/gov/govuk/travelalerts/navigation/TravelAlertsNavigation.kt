package uk.gov.govuk.travelalerts.navigation

import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import uk.gov.govuk.travelalerts.ui.countrylist.CountryListScreen
import uk.gov.govuk.travelalerts.ui.editcountries.EditCountriesScreen

const val COUNTRY_LIST_ROUTE = "country_list_route"
const val EDIT_COUNTRIES_ROUTE = "edit_countries_route"

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
        CountryListScreen(onClose = { navController.popBackStack() })
    }
    composable(route = EDIT_COUNTRIES_ROUTE) {
        EditCountriesScreen(
            onBack = { navController.popBackStack() },
            onFollowAnotherCountry = { navController.navigate(COUNTRY_LIST_ROUTE) },
            modifier = modifier
        )
    }
}
