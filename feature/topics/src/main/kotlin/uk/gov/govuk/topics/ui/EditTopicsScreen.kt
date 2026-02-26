package uk.gov.govuk.topics.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.ExtraLargeVerticalSpacer
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.component.ModalHeader
import uk.gov.govuk.design.ui.component.SmallVerticalSpacer
import uk.gov.govuk.design.ui.model.HeaderActionStyle
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.topics.EditTopicsViewModel
import uk.gov.govuk.topics.R
import uk.gov.govuk.topics.ui.component.TopicSelectionCard
import uk.gov.govuk.topics.ui.model.TopicItemUi

@Composable
internal fun EditTopicsRoute(
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: EditTopicsViewModel = hiltViewModel()
    val topics by viewModel.topics.collectAsState()

    EditTopicsScreen(
        topics = topics,
        onPageView = { title -> viewModel.onPageView(title) },
        onDone = onDone,
        onTopicSelectedChanged = { ref, title, isSelected ->
            viewModel.onTopicSelectedChanged(
                ref = ref,
                title = title,
                isSelected = isSelected
            )
        },
        modifier = modifier
    )
}

@Composable
private fun EditTopicsScreen(
    topics: List<TopicItemUi>?,
    onPageView: (String) -> Unit,
    onDone: () -> Unit,
    onTopicSelectedChanged: (String, String, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val title = stringResource(R.string.edit_title)
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        onPageView(title)
        focusRequester.requestFocus()
    }

    Column(modifier) {
        ModalHeader(
            text = title,
            actionStyle = HeaderActionStyle.ActionButton(
                title = stringResource(R.string.done_button),
                onClick = onDone
            ),
            modifier = Modifier.focusRequester(focusRequester)
        )
        LazyColumn(
            Modifier
                .padding(horizontal = GovUkTheme.spacing.medium)
        ) {
            item {
                Column{
                    MediumVerticalSpacer()
                    BodyRegularLabel(stringResource(R.string.edit_message))
                    MediumVerticalSpacer()
                }
            }

            if (!topics.isNullOrEmpty()) {
                itemsIndexed(topics) { _, topic ->
                    Column {
                        SmallVerticalSpacer()
                        TopicSelectionCard(
                            icon = topic.icon,
                            title = topic.title,
                            isSelected = topic.isSelected,
                            onClick = {
                                onTopicSelectedChanged(
                                    topic.ref,
                                    topic.title,
                                    !topic.isSelected
                                )
                            }
                        )
                        SmallVerticalSpacer()
                    }
                }

                item {
                    ExtraLargeVerticalSpacer()
                }
            }
        }
    }
}
