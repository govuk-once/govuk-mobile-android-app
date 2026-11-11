package uk.gov.govuk.chatv2.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import uk.gov.govuk.chatv2.ui.ChatRouteV2

const val CHAT_GRAPH_ROUTE = "chat_graph_route"
const val CHAT_ROUTE = "chat_route"

val chatDeepLinks = mapOf("/chat" to listOf(CHAT_ROUTE))

fun NavGraphBuilder.chatV2Graph(
    navController: NavHostController,
    launchBrowser: (url: String) -> Unit,
    onAuthError: () -> Unit,
    modifier: Modifier = Modifier,
) {
    navigation(
        route = CHAT_GRAPH_ROUTE,
        startDestination = CHAT_ROUTE
    ) {
        composable(CHAT_ROUTE) {
            ChatRouteV2(
                    onShowOnboarding = {
                        //todo: use same onboarding as V1?
                    },
                    launchBrowser = launchBrowser,
                    onAuthError = onAuthError,
                    modifier = modifier
                )
        }
    }
}
