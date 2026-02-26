package uk.gov.govuk.topics.ui.widget

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import uk.gov.govuk.design.ui.component.CardListItem
import uk.gov.govuk.design.ui.component.CentredContentWithIcon
import uk.gov.govuk.design.ui.component.ConnectedButton
import uk.gov.govuk.design.ui.component.ConnectedButtonGroup
import uk.gov.govuk.design.ui.component.IconListItem
import uk.gov.govuk.design.ui.component.SectionHeadingLabel
import uk.gov.govuk.design.ui.component.error.ProblemMessage
import uk.gov.govuk.design.ui.model.IconListItemStyle
import uk.gov.govuk.design.ui.model.SectionHeadingLabelButton
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.topics.R
import uk.gov.govuk.topics.TopicsCategory
import uk.gov.govuk.topics.TopicsWidgetUiState
import uk.gov.govuk.topics.TopicsWidgetViewModel
import uk.gov.govuk.topics.ui.model.TopicItemUi

@Composable
fun TopicsWidget(
    onTopicClick: (String, String) -> Unit,
    onEditClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: TopicsWidgetViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    uiState?.let {
        TopicsWidgetContent(
            uiState = it,
            onView = { category, topics -> viewModel.onView(category, topics) },
            onTopicClick = { category, title, ref, index, count ->
                viewModel.onTopicSelectClick(
                    category = category,
                    title = title,
                    ref = ref,
                    selectedItemIndex = index,
                    topicCount = count
                )
                onTopicClick(ref, title)
            },
            onEditClick = onEditClick,
            modifier = modifier
        )
    }
}

@Composable
private fun TopicsWidgetContent(
    uiState: TopicsWidgetUiState,
    onView: (TopicsCategory, List<TopicItemUi>) -> Unit,
    onTopicClick: (TopicsCategory, String, String, Int, Int) -> Unit,
    onEditClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Header(
            displayEdit = uiState.allTopics.isNotEmpty(),
            onEditClick = onEditClick
        )
        if (uiState.allTopics.isNotEmpty()) {
            TopicsCard(
                uiState = uiState,
                onView = onView,
                onEditClick = onEditClick,
                onTopicClick = onTopicClick
            )
        } else {
            ErrorCard()
        }
    }
}

@Composable
private fun Header(
    displayEdit: Boolean,
    onEditClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val editButtonText = stringResource(R.string.edit_button)
    SectionHeadingLabel(
        modifier = modifier,
        title3 = stringResource(R.string.topics_widget_title),
        button = if (displayEdit) {
            SectionHeadingLabelButton(
                title = editButtonText,
                altText = stringResource(R.string.edit_button_alt_text),
                onClick = { onEditClick(editButtonText) }
            )
        } else null
    )
}

@Composable
private fun TopicsCard(
    uiState: TopicsWidgetUiState,
    onView: (TopicsCategory, List<TopicItemUi>) -> Unit,
    onEditClick: (String) -> Unit,
    onTopicClick: (TopicsCategory, String, String, Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var activeButtonState by rememberSaveable { mutableStateOf( ConnectedButton.FIRST) }

    val (category, topics) = when (activeButtonState) {
        ConnectedButton.FIRST -> TopicsCategory.YOUR to uiState.yourTopics
        ConnectedButton.SECOND -> TopicsCategory.ALL to uiState.allTopics
    }

    LaunchedEffect(activeButtonState) {
        onView(category, topics)
    }

    Column(modifier) {
        CardListItem(
            isLast = false,
            drawDivider = false
        ) {
            Box(
                Modifier
                    .padding(top = GovUkTheme.spacing.medium)
                    .padding(horizontal = GovUkTheme.spacing.medium)
            ) {
                ConnectedButtonGroup(
                    firstText = stringResource(R.string.your_topics),
                    secondText = stringResource(R.string.all_topics),
                    onActiveStateChange = { activeButton ->
                        activeButtonState = activeButton
                    },
                    activeButton = activeButtonState
                )
            }
        }

        TopicsList(
            topics = topics,
            onClick = { title, ref, index -> onTopicClick(category, title, ref, index, topics.size) },
            onEmptyClick = onEditClick
        )
    }
}

@Suppress("UnusedReceiverParameter")
@Composable
private fun ColumnScope.TopicsList(
    topics: List<TopicItemUi>,
    onClick: (String, String, Int) -> Unit,
    onEmptyClick: (String) -> Unit
) {
    if (topics.isEmpty()) {
        val emptyText = stringResource(R.string.empty_topics)
        CardListItem(
            onClick = { onEmptyClick(emptyText) },
            isFirst = false,
            isLast = true
        ) {
            CentredContentWithIcon(
                icon = uk.gov.govuk.design.R.drawable.ic_add,
                description = emptyText
            )
        }
    } else {
        topics.forEachIndexed { index, topic ->
            IconListItem(
                title = topic.title,
                icon = topic.icon,
                onClick = {
                    onClick(
                        topic.title,
                        topic.ref,
                        index + 1
                    )
                },
                style = IconListItemStyle.Bold,
                isFirst = false,
                isLast = index == topics.lastIndex
            )
        }
    }
}

@Composable
private fun ErrorCard(modifier: Modifier = Modifier) {
    OutlinedCard(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = GovUkTheme.colourScheme.surfaces.list),
        border = BorderStroke(width = 0.dp, color = Color.Unspecified)
    ) {
        ProblemMessage(description = stringResource(R.string.topics_error_message))
    }
}

@Preview(showBackground = true)
@Composable
private fun TopicsWidgetPreview() {
    val topics = listOf(
        TopicItemUi(
            "",
            R.drawable.ic_topic_default,
            "A really really really really really really long topic title",
            "",
            isSelected = true
        ),
        TopicItemUi(
            "",
            R.drawable.ic_topic_benefits,
            "Benefits",
            "",
            isSelected = true
        ),
        TopicItemUi(
            "",
            R.drawable.ic_topic_transport,
            "Driving",
            "",
            isSelected = true
        )
    )

    GovUkTheme {
        TopicsWidgetContent(
            uiState = TopicsWidgetUiState(
                yourTopics = topics,
                allTopics = topics
            ),
            onView = { _, _ -> },
            onTopicClick = { _, _, _, _, _ -> },
            onEditClick = { }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TopicsWidgetEmptyTopicsPreview() {
    GovUkTheme {
        TopicsWidgetContent(
            uiState = TopicsWidgetUiState(
                allTopics = emptyList(),
                yourTopics = emptyList()
            ),
            onView = { _, _ -> },
            onTopicClick = { _, _, _, _, _ -> },
            onEditClick = { }
        )
    }
}