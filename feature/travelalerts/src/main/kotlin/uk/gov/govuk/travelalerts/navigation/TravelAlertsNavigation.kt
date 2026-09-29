package uk.gov.govuk.travelalerts.navigation

import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import uk.gov.govuk.travelalerts.ui.countrylist.CountryListScreen
import uk.gov.govuk.travelalerts.ui.editcountries.EditCountriesScreen
import uk.gov.govuk.travelalerts.ui.notificationsprompt.NotificationsRationaleScreen

const val COUNTRY_LIST_ROUTE = "country_list_route"
const val EDIT_COUNTRIES_ROUTE = "edit_countries_route"
const val NOTIFICATIONS_RATIONALE_ROUTE = "notifications_rationale_route"
const val COUNTRY_SLUG_ARG = "countrySlug"
const val TRAVEL_ALERTS_FOLLOW_ERROR_KEY = "travel_alerts_follow_error"
const val SHOW_ERROR_ARG = "showError"

val travelAlertsDeepLinks = mapOf(
    "/travelalerts/edit" to listOf(EDIT_COUNTRIES_ROUTE)
)

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
            navController = navController
        )
    }
    composable(
        route = "$EDIT_COUNTRIES_ROUTE?$SHOW_ERROR_ARG={$SHOW_ERROR_ARG}",
        arguments = listOf(
            navArgument(SHOW_ERROR_ARG) {
                type = NavType.BoolType
                defaultValue = false
            }
        )
    ) {
        EditCountriesScreen(
            onBack = { navController.popBackStack() },
            onFollowAnotherCountry = { navController.navigate(COUNTRY_LIST_ROUTE) },
            modifier = modifier
        )
    }
    composable(
        route = "$NOTIFICATIONS_RATIONALE_ROUTE/{$COUNTRY_SLUG_ARG}",
        arguments = listOf(
            navArgument(COUNTRY_SLUG_ARG) {
                type = NavType.StringType
            }
        )
    ) { backStackEntry ->
        val countrySlug = backStackEntry.arguments?.getString(COUNTRY_SLUG_ARG) ?: ""
        NotificationsRationaleScreen(
            countrySlug = countrySlug,
            onBack = { navController.popBackStack(COUNTRY_LIST_ROUTE, inclusive = true) },
            navController = navController,
            launchBrowser = launchBrowser
        )
    }
}
