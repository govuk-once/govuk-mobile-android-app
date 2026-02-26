package uk.gov.govuk.topics.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.FixedDoubleButtonGroup
import uk.gov.govuk.design.ui.component.FullScreenHeader
import uk.gov.govuk.design.ui.component.SmallVerticalSpacer
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.topics.R
import uk.gov.govuk.topics.TopicSelectionUiState
import uk.gov.govuk.topics.TopicSelectionViewModel
import uk.gov.govuk.topics.ui.component.TopicSelectionCard

@Composable
internal fun TopicSelectionRoute(
    onDone: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: TopicSelectionViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    TopicSelectionScreen(
        uiState = uiState,
        onPageView = { title -> viewModel.onPageView(title) },
        onClick = { ref, title ->
            viewModel.onClick(ref, title)
        },
        onDone = { text ->
            viewModel.onDone(text)
            onDone()
        },
        onSkip = { text ->
            viewModel.onSkip(text)
            onSkip()
        },
        modifier = modifier
    )
}

@Composable
private fun TopicSelectionScreen(
    uiState: TopicSelectionUiState?,
    onPageView: (String) -> Unit,
    onClick: (ref: String, title: String) -> Unit,
    onDone: (String) -> Unit,
    onSkip: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val title = stringResource(R.string.topic_selection_title)

    LaunchedEffect(Unit) {
        onPageView(title)
    }

    Column(modifier.fillMaxSize()) {
        FullScreenHeader(
            text = title,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = GovUkTheme.spacing.medium)
        )

        SmallVerticalSpacer()

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = GovUkTheme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(GovUkTheme.spacing.medium)
        ) {
            BodyRegularLabel(
                text = stringResource(R.string.topic_selection_description),
            )

            // loop over topics and add dynamically, has been changed from LazyColumn to overcome its
            // talkback limitation with off-screen items
            uiState?.topics?.forEach { topic ->
                TopicSelectionCard(
                    icon = topic.icon,
                    title = topic.title,
                    isSelected = topic.isSelected,
                    onClick = {
                        onClick(topic.ref, topic.title)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            SmallVerticalSpacer()
        }

        uiState?.let {
            val doneButtonText = stringResource(R.string.done_button)
            val skipButtonText = stringResource(R.string.skip_button)
            val isDoneEnabled = it.isDoneEnabled

            FixedDoubleButtonGroup(
                primaryText = doneButtonText,
                onPrimary = { onDone(doneButtonText) },
                secondaryText = skipButtonText,
                onSecondary = { onSkip(skipButtonText) },
                modifier = Modifier
                    .background(GovUkTheme.colourScheme.surfaces.fixedContainer),
                primaryEnabled = isDoneEnabled
            )
        }
    }
}