package uk.gov.govuk.notificationcentre.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import uk.gov.govuk.data.notificationcentre.model.Notification
import uk.gov.govuk.notificationcentre.ui.NotificationCentreDetailRoute
import uk.gov.govuk.notificationcentre.ui.NotificationCentreRoute

const val NOTIFICATION_CENTRE_GRAPH_ROUTE = "notification_centre_graph_route"
private const val NOTIFICATION_CENTRE_ROUTE = "notification_centre_route"
const val NOTIFICATION_CENTRE_DETAIL_ROUTE = "notification_centre_detail_route"

const val NOTIFICATION_CENTRE_DETAIL_ID_ARG = "notificationID"
const val NOTIFICATION_CENTRE_GRAPH_START_DESTINATION = NOTIFICATION_CENTRE_ROUTE

val notificationCentreDeepLinks = mapOf(
    "/notificationcentre" to listOf(NOTIFICATION_CENTRE_ROUTE),
    "/notificationcentre/detail" to listOf(NOTIFICATION_CENTRE_DETAIL_ROUTE)
)

fun NavGraphBuilder.notificationCentreGraph(
    navController: NavController,
    launchBrowser: (url: String) -> Unit,
    modifier: Modifier
) {

    navigation(
        route = NOTIFICATION_CENTRE_GRAPH_ROUTE,
        startDestination = NOTIFICATION_CENTRE_GRAPH_START_DESTINATION
    ) {
        composable(
            NOTIFICATION_CENTRE_ROUTE
        ) {
            NotificationCentreRoute(modifier, onBack = {
              navController.popBackStack()
            }, onTapNotification = {
                navController.navigateToNotificationCentreDetail(it)
            })
        }

        composable("$NOTIFICATION_CENTRE_DETAIL_ROUTE/{$NOTIFICATION_CENTRE_DETAIL_ID_ARG}",
            arguments = listOf(
                navArgument(NOTIFICATION_CENTRE_DETAIL_ID_ARG) { type = NavType.StringType },
            )) {
            NotificationCentreDetailRoute(modifier, onBack = {
                navController.popBackStack()
            }, launchBrowser = launchBrowser)
        }
    }
}

fun NavController.navigateToNotificationCentre() {
    navigate(NOTIFICATION_CENTRE_ROUTE)
}

fun NavController.navigateToNotificationCentreDetail(notification: Notification) {
    navigate("$NOTIFICATION_CENTRE_DETAIL_ROUTE/${notification.id}")
}
