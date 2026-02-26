package uk.gov.govuk.topics.ui

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import uk.gov.govuk.design.ui.component.ChildPageHeader
import uk.gov.govuk.design.ui.component.DrillInCard
import uk.gov.govuk.design.ui.component.FocusableCard
import uk.gov.govuk.design.ui.component.IconListItem
import uk.gov.govuk.design.ui.component.LargeVerticalSpacer
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.component.SectionHeadingLabel
import uk.gov.govuk.design.ui.component.SmallHorizontalSpacer
import uk.gov.govuk.design.ui.component.SmallVerticalSpacer
import uk.gov.govuk.design.ui.component.Title
import uk.gov.govuk.design.ui.component.error.OfflineMessage
import uk.gov.govuk.design.ui.component.error.ProblemMessage
import uk.gov.govuk.design.ui.model.CardListItem
import uk.gov.govuk.design.ui.model.FocusableCardColours
import uk.gov.govuk.design.ui.model.HeaderDismissStyle
import uk.gov.govuk.design.ui.model.SectionHeadingLabelButton
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.topics.R
import uk.gov.govuk.topics.TopicUiState
import uk.gov.govuk.topics.TopicViewModel
import uk.gov.govuk.topics.extension.toTopicName
import uk.gov.govuk.topics.ui.model.TopicUi

@Composable
internal fun TopicRoute(
    onBack: () -> Unit,
    onExternalLink: (url: String, onExternalLink: Int) -> Unit,
    onStepByStepSeeAll: () -> Unit,
    onPopularPagesSeeAll: () -> Unit,
    onSubtopic: (ref: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: TopicViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val focusRequester = remember { FocusRequester() }

    Box(modifier.fillMaxSize()
        .background(GovUkTheme.colourScheme.surfaces.screenBackground)
    ) {
        uiState?.let {
            when (it) {
                is TopicUiState.Default -> {
                    TopicScreen(
                        topic = it.topicUi,
                        onPageView = { title -> viewModel.onPageView(
                            topicUi = it.topicUi,
                            title = title
                        ) },
                        onBack = onBack,
                        onExternalLink = { section, text, url, selectedItemIndex, totalItemCount ->
                            viewModel.onContentClick(
                                title = it.topicUi.title,
                                section = section,
                                text = text,
                                url = url,
                                selectedItemIndex = selectedItemIndex,
                                totalItemCount = totalItemCount
                            )
                            onExternalLink(url, selectedItemIndex)
                        },
                        onStepByStepSeeAll = { section, text ->
                            onStepByStepSeeAll()
                            viewModel.onSeeAllClick(
                                section = section,
                                text = text
                            )
                        },
                        onPopularPagesSeeAll = { section, text ->
                            onPopularPagesSeeAll()
                            viewModel.onSeeAllClick(
                                section = section,
                                text = text
                            )
                        },
                        onSubtopic = { text, ref, selectedItemIndex, totalItemCount ->
                            viewModel.onSubtopicClick(
                                text = text,
                                selectedItemIndex = selectedItemIndex,
                                totalItemCount = totalItemCount
                            )
                            onSubtopic(ref)
                        },
                        focusRequester = focusRequester
                    )
                }

                is TopicUiState.Offline -> ErrorScreen(
                    topicReference = it.topicReference,
                    onPageView = { title -> viewModel.onPageView(title = title) },
                    onBack = onBack,
                    content = { OfflineMessage({ viewModel.getTopic() }) }
                )


                is TopicUiState.ServiceError -> ErrorScreen(
                    topicReference = it.topicReference,
                    onPageView = { title -> viewModel.onPageView(title = title) },
                    onBack = onBack,
                    content = { ProblemMessage() }
                )
            }
        }
    }
}

@Composable
private fun TopicScreen(
    topic: TopicUi,
    onPageView: (String) -> Unit,
    onBack: () -> Unit,
    onExternalLink: (section: String, text: String, url: String, selectedItemIndex: Int, totalItemCount: Int) -> Unit,
    onStepByStepSeeAll: (section: String, text: String) -> Unit,
    onPopularPagesSeeAll: (section: String, text: String) -> Unit,
    onSubtopic: (text: String, ref: String, selectedItemIndex: Int, totalItemCount: Int) -> Unit,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    val popularPagesIndex = 1
    val stepByStepsIndex = popularPagesIndex + topic.popularPages.size
    val servicesIndex = stepByStepsIndex + topic.stepBySteps.size
    val subtopicsIndex = servicesIndex + topic.services.size

    val totalItemCount = topic.popularPages.size +
            topic.stepBySteps.size +
            topic.services.size +
            topic.subtopics.size

    val onExternalLinkClick: (section: String, text: String, url: String, selectedItemIndex: Int) -> Unit = {
            section, text, url, selectedItemIndex ->
        onExternalLink(section, text, url, selectedItemIndex, totalItemCount)
    }

    LaunchedEffect(Unit) {
        onPageView(topic.title)
    }

    Column(
        modifier
            .fillMaxWidth()
            .background(GovUkTheme.colourScheme.surfaces.screenBackground)
    ) {

        ChildPageHeader(
            dismissStyle = HeaderDismissStyle.Back(onBack)
        )

        val popularPagesSection = TopicUi.Section(
            title = R.string.popular_pages_title,
            icon = R.drawable.ic_topic_popular
        )

        val stepByStepSection = TopicUi.Section(
            title = R.string.step_by_step_guides_title,
            icon = R.drawable.ic_topic_step_by_step
        )
        val servicesSection = TopicUi.Section(
            title = R.string.services_title,
            icon = R.drawable.ic_topic_services_and_info
        )

        LazyColumn {
            item {
                Title(
                    title = topic.title,
                    modifier = Modifier.focusRequester(focusRequester),
                    description = topic.description
                )
            }

            item {
                MediumVerticalSpacer()
            }

            val showHorizontalScrollView = false

            if (showHorizontalScrollView) {
                item {
                    HorizontalScrollView(
                        topic = topic,
                        startIndex = popularPagesIndex,
                        onExternalLink = onExternalLinkClick,
                        onPopularPagesSeeAll = onPopularPagesSeeAll,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            } else {
                contentItems(
                    startIndex = popularPagesIndex,
                    contentItems = topic.popularPages,
                    section = popularPagesSection,
                    onClick = onExternalLinkClick
                )
            }

            contentItems(
                startIndex = stepByStepsIndex,
                contentItems = topic.stepBySteps,
                section = stepByStepSection,
                onClick = onExternalLinkClick,
                displaySeeAll = topic.displayStepByStepSeeAll,
                onSeeAll = onStepByStepSeeAll
            )

            contentItems(
                startIndex = servicesIndex,
                contentItems = topic.services,
                section = servicesSection,
                onClick = onExternalLinkClick
            )

            subtopics(
                startIndex = subtopicsIndex,
                subtopics = topic.subtopics,
                section = topic.subtopicsSection,
                onClick = { text, ref, selectedItemIndex ->
                    onSubtopic(text, ref, selectedItemIndex, totalItemCount)
                }
            )
        }
    }
}

@Composable
private fun HorizontalScrollView(
    topic: TopicUi,
    startIndex: Int,
    onExternalLink: (section: String, text: String, url: String, selectedItemIndex: Int) -> Unit,
    onPopularPagesSeeAll: (section: String, text: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val fontScale = LocalDensity.current.fontScale
    val isFontScaledUp = fontScale > 1.0f
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    val section = stringResource(R.string.popular_pages_title)
    val seeAllButton = stringResource(R.string.see_all_button)
    val cards = topic.popularPages.mapIndexed { index, topic ->
        CardListItem(
            title = topic.title,
            onClick = {
                onExternalLink(
                    section, topic.title, topic.url, startIndex + index
                )
            }
        )
    }
    val colourMapper = @Composable { cardColour: FocusableCardColours ->
        when (cardColour) {
            FocusableCardColours.Focussed.Background -> GovUkTheme.colourScheme.surfaces.cardCarouselFocused
            FocusableCardColours.Focussed.Content -> GovUkTheme.colourScheme.textAndIcons.cardCarouselFocused
            FocusableCardColours.UnFocussed.Background -> GovUkTheme.colourScheme.surfaces.cardCarousel
            FocusableCardColours.UnFocussed.Content -> GovUkTheme.colourScheme.textAndIcons.cardCarousel
            else -> {
                Color.Transparent
            }
        }
    }

    SectionHeadingLabel(
        modifier = modifier.padding(horizontal = GovUkTheme.spacing.medium),
        title3 = section,
        button = if (!isFontScaledUp && !isLandscape) {
            SectionHeadingLabelButton(
                title = seeAllButton,
                altText = "$seeAllButton $section",
                onClick = {
                    onPopularPagesSeeAll(section, seeAllButton)
                }
            )
        } else null
    )

    if (isFontScaledUp) {
        // vertical list
        Column(
            modifier = modifier.fillMaxWidth()
                .padding(horizontal = GovUkTheme.spacing.medium)
        ) {
            cards.forEach { item ->
                FocusableCard(
                    item,
                    modifier = Modifier.padding(bottom = GovUkTheme.spacing.medium),
                    colourMapper = colourMapper
                )
            }
        }
    } else {
        // horizontal list
        Box {
            LazyRow(
                modifier = modifier.fillMaxWidth()
                    .padding(horizontal = GovUkTheme.spacing.medium),
            ) {
                itemsIndexed(cards) { index, item ->
                    FocusableCard(
                        item,
                        modifier = Modifier.size(150.dp),
                        colourMapper = colourMapper
                    )

                    if (index < cards.size - 1) {
                        SmallHorizontalSpacer()
                    }
                }
            }
        }
    }

    MediumVerticalSpacer()
}

private fun LazyListScope.contentItems(
    startIndex: Int,
    contentItems: List<TopicUi.TopicContent>,
    section: TopicUi.Section,
    onClick: (section: String, text: String, url: String, selectedItemIndex: Int) -> Unit,
    displaySeeAll: Boolean = false,
    onSeeAll: (section: String, text: String) -> Unit = { _, _ -> }
) {
    if (contentItems.isNotEmpty()) {
        item {
            val sectionTitle = stringResource(section.title)
            val seeAllButton = stringResource(R.string.see_all_button)

            SectionHeadingLabel(
                modifier = Modifier.padding(horizontal = GovUkTheme.spacing.medium),
                title3 = sectionTitle,
                button = if (displaySeeAll) {
                    SectionHeadingLabelButton(
                        title = seeAllButton,
                        altText = "$seeAllButton $sectionTitle",
                        onClick = {
                            onSeeAll(
                                sectionTitle,
                                seeAllButton
                            )
                        },
                    )
                } else null
            )
        }
        itemsIndexed(contentItems) { index, content ->
            val sectionTitle = stringResource(section.title)
            IconListItem(
                title = content.title,
                icon = section.icon,
                onClick = { onClick(sectionTitle, content.title, content.url, startIndex + index) },
                modifier = Modifier.padding(horizontal = GovUkTheme.spacing.medium),
                isFirst = index == 0,
                isLast = index == contentItems.lastIndex,
                altText = "${content.title}. ${stringResource(uk.gov.govuk.design.R.string.opens_in_web_browser)}"
            )
        }

        item {
            LargeVerticalSpacer()
        }
    }
}

private fun LazyListScope.subtopics(
    startIndex: Int,
    subtopics: List<TopicUi.Subtopic>,
    section: TopicUi.Section,
    onClick: (text: String, ref: String, selectedItemIndex: Int) -> Unit,
) {
    if (subtopics.isNotEmpty()) {
        item {
            SectionHeadingLabel(
                modifier = Modifier.padding(horizontal = GovUkTheme.spacing.medium),
                title3 = stringResource(section.title)
            )
        }

        itemsIndexed(subtopics) { index, subtopic ->
            DrillInCard(
                title = subtopic.title,
                onClick = { onClick(subtopic.title, subtopic.ref, startIndex + index) },
                modifier = Modifier.padding(horizontal = GovUkTheme.spacing.medium)
            )
            if (index < subtopics.size - 1) {
                SmallVerticalSpacer()
            }
        }

        item {
            MediumVerticalSpacer()
        }
    }
}

@Composable
private fun ErrorScreen(
    topicReference: String,
    onPageView: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val topicName = topicReference.toTopicName(LocalContext.current)

    Column(modifier.verticalScroll(rememberScrollState())) {
        LaunchedEffect(Unit) {
            onPageView(topicName)
        }

        ChildPageHeader(
            text = topicName,
            dismissStyle = HeaderDismissStyle.Back(onBack)
        )

        content()
    }
}

@Preview
@Composable
private fun ErrorScreenOfflinePreview() {
    GovUkTheme {
        ErrorScreen(
            topicReference = "benefits",
            onPageView = {},
            onBack = {},
            content = { OfflineMessage({}) }
        )
    }
}

@Preview
@Composable
private fun ErrorScreenProblemPreview() {
    GovUkTheme {
        ErrorScreen(
            topicReference = "benefits",
            onPageView = {},
            onBack = {},
            content = { ProblemMessage() }
        )
    }
}
