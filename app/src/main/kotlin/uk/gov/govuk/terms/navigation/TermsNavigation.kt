package uk.gov.govuk.terms.navigation

import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation

const val TERMS_GRAPH_ROUTE = "terms_graph_route"
private const val TERMS_ROUTE = "terms_route"

fun NavGraphBuilder.termsGraph(
    navController: NavController,
    onCompleted: () -> Unit,
    modifier: Modifier = Modifier
) {
    navigation(
        route = TERMS_GRAPH_ROUTE,
        startDestination = TERMS_ROUTE
    ) {
        composable(route = TERMS_ROUTE) {
            Text("Blah blah blah")
        }
    }
}