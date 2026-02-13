package uk.gov.govuk.notificationcentre.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import uk.gov.govuk.notificationcentre.ui.NotificationCentreRoute

const val NOTIFICATION_CENTRE_GRAPH_ROUTE = "notification_centre_graph_route"
private const val NOTIFICATION_CENTRE_ROUTE = "notification_centre_route"
const val NOTIFICATION_CENTRE_GRAPH_START_DESTINATION = NOTIFICATION_CENTRE_ROUTE

val notificationCentreDeepLinks = mapOf("/notification" to listOf(NOTIFICATION_CENTRE_ROUTE))

fun NavGraphBuilder.notificationCentreGraph(
    modifier: Modifier
) {

    navigation(
        route = NOTIFICATION_CENTRE_GRAPH_ROUTE,
        startDestination = NOTIFICATION_CENTRE_GRAPH_START_DESTINATION
    ) {
        composable(
            NOTIFICATION_CENTRE_ROUTE
        ) {
            NotificationCentreRoute(modifier)
        }
    }
}

fun NavController.navigateToNotificationCentre() {
    navigate(NOTIFICATION_CENTRE_ROUTE)
}
