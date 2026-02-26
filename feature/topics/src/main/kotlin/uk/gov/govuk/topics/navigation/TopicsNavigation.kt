package uk.gov.govuk.topics.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import uk.gov.govuk.topics.ui.AllPopularPagesRoute
import uk.gov.govuk.topics.ui.AllStepByStepRoute
import uk.gov.govuk.topics.ui.EditTopicsRoute
import uk.gov.govuk.topics.ui.TopicRoute
import uk.gov.govuk.topics.ui.TopicSelectionRoute

const val TOPIC_SELECTION_GRAPH_ROUTE = "topic_selection_graph_route"
private const val TOPIC_SELECTION_ROUTE = "topic_selection_route"
const val TOPICS_GRAPH_ROUTE = "topics_graph_route"
const val TOPIC_ROUTE = "topic_route"
internal const val TOPIC_REF_ARG = "ref"
internal const val TOPIC_SUBTOPIC_ARG = "isSubtopic"
const val TOPICS_EDIT_ROUTE = "topics_edit_route"
const val TOPICS_ALL_STEP_BY_STEPS_ROUTE = "topics_all_step_by_steps_route"
const val TOPICS_ALL_POPULAR_PAGES_ROUTE = "topics_all_popular_pages_route"

val topicsDeepLinks = mapOf(
    // Todo - individual topic with args
    "/topics/edit" to listOf(TOPICS_EDIT_ROUTE)
)

fun NavGraphBuilder.topicSelectionGraph(
    topicSelectionCompleted: () -> Unit,
) {
    navigation(
        route = TOPIC_SELECTION_GRAPH_ROUTE,
        startDestination = TOPIC_SELECTION_ROUTE
    ) {
        composable(TOPIC_SELECTION_ROUTE) {
            TopicSelectionRoute(
                onDone = topicSelectionCompleted,
                onSkip = topicSelectionCompleted
            )
        }
    }
}

fun NavGraphBuilder.topicsGraph(
    navController: NavController,
    launchBrowser: (url: String) -> Unit,
    modifier: Modifier = Modifier
) {
    navigation(
        route = TOPICS_GRAPH_ROUTE,
        startDestination = TOPICS_EDIT_ROUTE
    ) {
        val topicPath = "/{$TOPIC_REF_ARG}?$TOPIC_SUBTOPIC_ARG={$TOPIC_SUBTOPIC_ARG}"
        composable(TOPICS_EDIT_ROUTE) {
            EditTopicsRoute(
                onDone = { navController.popBackStack() }
            )
        }
        composable(
            "$TOPIC_ROUTE$topicPath",
            arguments = listOf(
                navArgument(TOPIC_REF_ARG) { type = NavType.StringType },
                navArgument(TOPIC_SUBTOPIC_ARG) { type = NavType.BoolType },
            ),
            /* deepLinks = deepLinks("/topics$topicPath") */
        ) {
            TopicRoute(
                onBack = { navController.popBackStack() },
                onExternalLink = { url, _ ->
                    launchBrowser(url)
                },
                onStepByStepSeeAll = { navController.navigate(TOPICS_ALL_STEP_BY_STEPS_ROUTE) },
                onPopularPagesSeeAll = { navController.navigate(TOPICS_ALL_POPULAR_PAGES_ROUTE) },
                onSubtopic = { ref -> navController.navigateToTopic(ref, true) },
                modifier = modifier
            )
        }
        composable(TOPICS_ALL_STEP_BY_STEPS_ROUTE) {
            AllStepByStepRoute(
                onBack = { navController.popBackStack()},
                onClick = { url ->
                    launchBrowser(url)
                 },
                modifier = modifier
            )
        }
        composable(TOPICS_ALL_POPULAR_PAGES_ROUTE) {
            AllPopularPagesRoute(
                onBack = { navController.popBackStack()},
                onClick = { url ->
                    launchBrowser(url)
                },
                modifier = modifier
            )
        }
    }
}

fun NavController.navigateToTopic(ref: String, isSubtopic: Boolean = false) {
    navigate("$TOPIC_ROUTE/$ref?$TOPIC_SUBTOPIC_ARG=$isSubtopic")
}

fun NavController.navigateToTopicsEdit() {
    navigate(TOPICS_EDIT_ROUTE)
}
