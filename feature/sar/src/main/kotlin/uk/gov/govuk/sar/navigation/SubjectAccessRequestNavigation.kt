package uk.gov.govuk.sar.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import uk.gov.govuk.sar.ui.SubjectAccessRequestDisplayRoute
import uk.gov.govuk.sar.ui.SubjectAccessRequestRoute

const val SUBJECT_ACCESS_REQUEST_GRAPH_ROUTE = "subject_access_request_graph_route"
const val SUBJECT_ACCESS_REQUEST_ROUTE = "subject_access_request_route"
const val SUBJECT_ACCESS_REQUEST_DISPLAY_ROUTE = "subject_access_request_display_route"

fun NavGraphBuilder.subjectAccessRequestGraph(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    navigation(
        route = SUBJECT_ACCESS_REQUEST_GRAPH_ROUTE,
        startDestination = SUBJECT_ACCESS_REQUEST_ROUTE
    ) {
        composable(SUBJECT_ACCESS_REQUEST_ROUTE) {
            SubjectAccessRequestRoute(
                onConfirm = { navController.navigate(SUBJECT_ACCESS_REQUEST_DISPLAY_ROUTE) },
                onClose = { popToSettingsScreen(navController) },
                modifier = modifier
            )
        }
        composable(SUBJECT_ACCESS_REQUEST_DISPLAY_ROUTE) {
            SubjectAccessRequestDisplayRoute(
                onClose = { popToSettingsScreen(navController) },
                modifier = modifier
            )
        }
    }
}

private fun popToSettingsScreen(navController: NavController) {
    navController.popBackStack(route = "settings_route", inclusive = false)
}
