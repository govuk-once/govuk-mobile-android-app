package uk.gov.govuk.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import uk.gov.govuk.R
import uk.gov.govuk.chat.navigation.CHAT_GRAPH_ROUTE
import uk.gov.govuk.design.R.drawable.ic_chat
import uk.gov.govuk.design.R.drawable.ic_home
import uk.gov.govuk.design.R.drawable.ic_settings
import uk.gov.govuk.home.navigation.HOME_GRAPH_ROUTE
import uk.gov.govuk.settings.navigation.SETTINGS_GRAPH_ROUTE
import uk.gov.govuk.topics.navigation.TOPICS_ALL_STEP_BY_STEPS_ROUTE
import uk.gov.govuk.topics.navigation.TOPIC_ROUTE
import uk.gov.govuk.visited.navigation.VISITED_ROUTE


internal sealed class TopLevelDestination(
    val route: String,
    @StringRes val stringResId: Int,
    @DrawableRes val icon: Int,
    val associatedRoutes: List<String> = emptyList()
) {
    internal data object Home : TopLevelDestination(
        route =  HOME_GRAPH_ROUTE,
        stringResId = R.string.home,
        icon = ic_home,
        associatedRoutes = listOf(
            VISITED_ROUTE,
            TOPIC_ROUTE,
            TOPICS_ALL_STEP_BY_STEPS_ROUTE
        )
    )

    internal data object Chat : TopLevelDestination(
        route = CHAT_GRAPH_ROUTE,
        stringResId = R.string.chat,
        icon = ic_chat
    )

    internal data object Settings : TopLevelDestination(
        route = SETTINGS_GRAPH_ROUTE,
        stringResId = R.string.settings,
        icon = ic_settings
    )

    companion object {
        fun values(isChatEnabled: Boolean): List<TopLevelDestination> {
            return buildList {
                add(Home)
                if (isChatEnabled) add(Chat)
                add(Settings)
            }
        }
    }
}
