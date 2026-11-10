package uk.gov.govuk.messages.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import uk.gov.govuk.messages.data.model.Notification
import uk.gov.govuk.messages.ui.MessagesDetailRoute
import uk.gov.govuk.messages.ui.MessagesRoute

const val MESSAGES_GRAPH_ROUTE = "messages_graph_route"
const val MESSAGES_ROUTE = "messages_route"
const val MESSAGES_DETAIL_ROUTE = "messages_detail_route"

const val MESSAGES_DETAIL_ID_ARG = "notificationID"
const val MESSAGES_GRAPH_START_DESTINATION = MESSAGES_ROUTE

val messagesDeepLinks = mapOf(
    "/notificationcentre/detail" to listOf(MESSAGES_DETAIL_ROUTE)
)

fun NavGraphBuilder.messagesGraph(
    navController: NavController,
    launchBrowser: (url: String) -> Unit,
    modifier: Modifier
) {

    navigation(
        route = MESSAGES_GRAPH_ROUTE,
        startDestination = MESSAGES_GRAPH_START_DESTINATION
    ) {
        composable(
            MESSAGES_ROUTE
        ) {
            MessagesRoute(modifier, onBack = {
              navController.popBackStack()
            }, onTapNotification = {
                navController.navigateToMessagesDetail(it)
            })
        }

        composable("$MESSAGES_DETAIL_ROUTE/{$MESSAGES_DETAIL_ID_ARG}",
            arguments = listOf(
                navArgument(MESSAGES_DETAIL_ID_ARG) { type = NavType.StringType },
            )) {
            MessagesDetailRoute(modifier, onBack = {
                navController.popBackStack()
            }, launchBrowser = launchBrowser)
        }
    }
}

fun NavController.navigateToMessages() {
    navigate(MESSAGES_ROUTE)
}

fun NavController.navigateToMessagesDetail(message: Notification) {
    navigate("$MESSAGES_DETAIL_ROUTE/${message.id}")
}
