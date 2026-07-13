package uk.gov.govuk.widgets.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import uk.gov.govuk.config.data.local.model.HomeWidget
import uk.gov.govuk.config.data.remote.model.Link
import uk.gov.govuk.config.data.remote.model.PromoBanner
import uk.gov.govuk.config.data.remote.model.PromoBannerType
import uk.gov.govuk.topics.navigation.navigateToTopic
import uk.gov.govuk.topics.navigation.navigateToTopicsEdit
import uk.gov.govuk.topics.ui.widget.TopicsWidget
import uk.gov.govuk.visited.navigation.VISITED_GRAPH_ROUTE
import uk.gov.govuk.visited.ui.widget.VisitedWidget
import uk.govuk.app.local.navigation.LOCAL_GRAPH_ROUTE
import uk.govuk.app.local.navigation.LOCAL_LOOKUP_ROUTE
import uk.govuk.app.local.ui.LocalWidget

internal fun List<HomeWidget>?.contains(widget: HomeWidget) = this?.contains(widget) == true

internal fun homeWidgets(
    navController: NavHostController,
    homeWidgets: List<HomeWidget>?,
    onInternalClick: (text: String) -> Unit,
    onExternalClick: (text: String, url: String?) -> Unit,
    onSuppressClick: (id: String, text: String) -> Unit,
    launchBrowser: (url: String) -> Unit
): List<@Composable (Modifier) -> Unit> {
    val widgets = mutableListOf<@Composable (Modifier) -> Unit>()
    homeWidgets?.forEach {
        when (it) {
            is HomeWidget.Banner -> {
                widgets.add { modifier ->
                    EmergencyBanner(
                        emergencyBanner = it.emergencyBanner,
                        onClick = { text, url ->
                            onExternalClick(text, url)
                        },
                        launchBrowser = launchBrowser,
                        onSuppressClick = onSuppressClick,
                        modifier = modifier
                    )
                }
            }

            is HomeWidget.Chat -> {
                widgets.add { modifier ->
                    val promoBanner = PromoBanner(
                        id = it.chatBanner.id,
                        title = it.chatBanner.title,
                        body = it.chatBanner.body,
                        link = Link(
                            title = it.chatBanner.link.title,
                            url = "govuk://gov.uk/chat" // hard code for now
                        ),
                        image = "background_chat_banner", // hard code for now
                        type = PromoBannerType.INTERNAL
                    )

                    PromoBanner(
                        promoBanner = promoBanner,
                        onClick = { text, url ->
                            onInternalClick(text)
                            if (url != null) {
                                launchBrowser(url)
                            }
                        },
                        onDismiss = { text ->
                            onSuppressClick(promoBanner.id, text)
                        },
                        modifier = modifier
                    )
                }
            }

            is HomeWidget.Promo -> {
                widgets.add { modifier ->
                    PromoBanner(
                        promoBanner = it.promoBanner,
                        onClick = { text, url ->
                            if (it.promoBanner.type == PromoBannerType.EXTERNAL) {
                                onExternalClick(text, url)
                            } else {
                                onInternalClick(text)
                            }
                            if (url != null) {
                                launchBrowser(url)
                            }
                        },
                        onDismiss = { text ->
                            onSuppressClick(it.promoBanner.id, text)
                        },
                        modifier = modifier
                    )
                }
            }

            is HomeWidget.RecentActivity -> {
                widgets.add { modifier ->
                    VisitedWidget(
                        onSeeAllClick = { text ->
                            onInternalClick(text)
                            navController.navigate(VISITED_GRAPH_ROUTE)
                        },
                        launchBrowser = launchBrowser,
                        modifier = modifier
                    )
                }
            }

            is HomeWidget.Topics -> {
                widgets.add { modifier ->
                    TopicsWidget(
                        onTopicClick = { ref, title ->
                            onInternalClick(title)
                            navController.navigateToTopic(ref)
                        },
                        onEditClick = { text ->
                            onInternalClick(text)
                            navController.navigateToTopicsEdit()
                        },
                        modifier = modifier
                    )
                }
            }

            is HomeWidget.Local -> {
                widgets.add { modifier ->
                    LocalWidget(
                        onLookupClick = { text ->
                            onInternalClick(text)
                            navController.navigate(LOCAL_GRAPH_ROUTE)
                        },
                        onLocalAuthorityClick = { text, url ->
                            onExternalClick(text, url)
                        },
                        onEditClick = { text ->
                            onInternalClick(text)
                            navController.navigate(LOCAL_LOOKUP_ROUTE)
                        },
                        launchBrowser = launchBrowser,
                        modifier = modifier
                    )
                }
            }

            is HomeWidget.UserFeedback -> {
                widgets.add { modifier ->
                    val userFeedbackBanner = it.userFeedbackBanner
                    UserFeedbackBanner(
                        userFeedbackBanner = userFeedbackBanner,
                        onClick = {
                            launchBrowser(userFeedbackBanner.link.url)
                            onExternalClick(
                                userFeedbackBanner.link.title,
                                userFeedbackBanner.link.url
                            )
                        },
                        modifier = modifier
                    )
                }
            }
            else -> { /* Do nothing */ }
        }
    }
    return widgets
}
