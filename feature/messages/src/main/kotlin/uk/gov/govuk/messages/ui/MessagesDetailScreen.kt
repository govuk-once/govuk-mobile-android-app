package uk.gov.govuk.messages.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.text
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import uk.gov.govuk.design.ui.component.BodyBoldLabel
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.RunOnceLaunchedEffect
import uk.gov.govuk.design.ui.component.Title1BoldLabel
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.messages.MessagesDetailUiState
import uk.gov.govuk.messages.MessagesDetailViewModel
import uk.gov.govuk.messages.R
import uk.gov.govuk.messages.data.model.Notification
import uk.gov.govuk.messages.data.model.NotificationFixtures.Companion.mockNotifications
import uk.gov.govuk.messages.ui.component.Markdown

@Composable
internal fun MessagesDetailRoute(
    modifier: Modifier = Modifier, onBack: () -> Unit, launchBrowser: (url: String) -> Unit
) {
    val viewModel: MessagesDetailViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier
            .fillMaxSize()
            .background(GovUkTheme.colourScheme.surfaces.fullScreen)
            .safeDrawingPadding()
    ) {
        MessagesDetailScreen(
            {
            viewModel.onPageView()
        },
            uiState,
            actions = MessagesDetailActions(
                onBack = onBack,
                onUnread = {
                    viewModel.onTapMarkUnread()
                    onBack()
                },
                onTapDelete = {
                    viewModel.onTapDelete()
                },
                onCancelDelete = {
                    viewModel.onCancelDelete()
                },
                onConfirmDelete = {
                    viewModel.onConfirmDelete()
                    onBack()
                },
                launchBrowser = {
                    launchBrowser(it)
                    viewModel.onLinkTap(it)
                }
            ),
            showDeleteConfirmation = (uiState as? MessagesDetailUiState.Loaded)?.showDeleteConfirmation
                ?: false)
    }
}

private class MessagesDetailActions(
    val onBack: () -> Unit,
    val onUnread: () -> Unit,
    val onTapDelete: () -> Unit,
    val onCancelDelete: () -> Unit,
    val onConfirmDelete: () -> Unit,
    val launchBrowser: (url: String) -> Unit,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MessagesDetailScreen(
    onPageView: () -> Unit,
    state: MessagesDetailUiState,
    actions: MessagesDetailActions,
    showDeleteConfirmation: Boolean
) {
    var showDeleteConfirmationDialog by remember { mutableStateOf(false) }

    LaunchedEffect(showDeleteConfirmation) {
        showDeleteConfirmationDialog = showDeleteConfirmation
    }

    Column(
        Modifier.fillMaxWidth(),
        Arrangement.Center,
        Alignment.CenterHorizontally
    ) {
        Header(
            onBack = actions.onBack,
            onUnread = actions.onUnread,
            onDelete = {
                actions.onTapDelete()
            }
        )

        Column(
            Modifier.fillMaxWidth()
        ) {
            when (state) {
                is MessagesDetailUiState.Loading -> MessagesDetailScreenLoading()
                is MessagesDetailUiState.NoInternet -> MessagesScreenNoInternet()
                is MessagesDetailUiState.Error -> MessagesScreenError()
                is MessagesDetailUiState.Loaded -> MessagesDetailScreenLoaded(
                    state.message, actions.launchBrowser
                )
                else -> {}
            }
        }

        if (showDeleteConfirmationDialog) {
            ConfirmationDialog(
                actions.onConfirmDelete,
                actions.onCancelDelete
            )
        }

        RunOnceLaunchedEffect {
            onPageView()
        }
    }
}

@Composable
private fun Header(
    actionColour: Color = GovUkTheme.colourScheme.textAndIcons.iconSecondary,
    onBack: () -> Unit,
    onUnread: () -> Unit,
    onDelete: () -> Unit
) {
    Column {
        Row(
            Modifier
                .height(64.dp)
                .padding(end = GovUkTheme.spacing.medium)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(
                onClick = onBack
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    stringResource(uk.gov.govuk.design.R.string.content_desc_back),
                    tint = actionColour
                )
            }

            Spacer(Modifier.weight(1f))

            IconButton(
                onUnread,
                Modifier.size(48.dp)
            ) {
                Icon(
                    painterResource(id = R.drawable.ic_mark_unread),
                    stringResource(R.string.mark_as_unread),
                    tint = actionColour,
                )
            }

            IconButton(
                onDelete, Modifier.size(48.dp)

            ) {
                Icon(
                    painterResource(id = R.drawable.ic_delete_notification),
                    stringResource(R.string.delete_notification),
                    tint = actionColour,
                )
            }
        }
    }
}

@Composable
private fun MessagesDetailScreenLoading() {
    Box(
        Modifier.fillMaxSize(),
        Alignment.Center
    ) {
        val loadingContentDescription = stringResource(R.string.loading_content_description)
        CircularProgressIndicator(
            modifier = Modifier
                .size(36.dp)
                .semantics {
                    text = AnnotatedString(loadingContentDescription)
                },
            color = GovUkTheme.colourScheme.surfaces.primary,
        )
    }
}

@Composable
private fun MessagesDetailScreenLoaded(
    message: Notification, launchBrowser: (url: String) -> Unit
) {
    val headerContentDescription = stringResource(
        R.string.notification_detail_header_content_description,
        message.detailFormattedDate,
        message.metadata.sender.displayName
    )

    Column(
        modifier = Modifier
            .padding(
                horizontal = GovUkTheme.spacing.medium, vertical = GovUkTheme.spacing.medium
            )
            .verticalScroll(
                rememberScrollState()
            )
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(GovUkTheme.colourScheme.surfaces.cardMsgHeader)
                .padding(16.dp)
                .clearAndSetSemantics {
                    heading()
                    contentDescription = headerContentDescription
                }) {
            BodyRegularLabel(
                message.detailFormattedDate,
                Modifier.padding(bottom = 4.dp),
                GovUkTheme.colourScheme.textAndIcons.secondary
            )

            BodyBoldLabel(
                message.metadata.sender.displayName,
                color = GovUkTheme.colourScheme.textAndIcons.primary,
            )
        }

        Column(modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp)) {

            Title1BoldLabel(
                message.messageTitle ?: message.title,
                Modifier.padding(top = 24.dp, bottom = 24.dp),
                GovUkTheme.colourScheme.textAndIcons.primary
            )

            Markdown(
                message.messageBody ?: message.body, onLinkClick = { url ->
                    launchBrowser(url)
                })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConfirmationDialog(onConfirm: () -> Unit, onCancel: () -> Unit) {
    AlertDialog(
        shape = RoundedCornerShape(GovUkTheme.numbers.cornerAndroidList), title = {
        BodyBoldLabel(stringResource(R.string.delete_notification_sheet_title))
    }, text = {
        BodyRegularLabel(stringResource(R.string.delete_notification_sheet_body))
    }, onDismissRequest = {
        onCancel()
    }, confirmButton = {
        TextButton(
            onClick = {
                onConfirm()
            }) {
            BodyRegularLabel(
                stringResource(R.string.delete_notification_sheet_confirm),
                color = GovUkTheme.colourScheme.textAndIcons.buttonDestructive
            )
        }
    }, dismissButton = {
        TextButton(
            onClick = {
                onCancel()
            }) {
            BodyRegularLabel(
                stringResource(R.string.delete_notification_sheet_cancel),
                color = GovUkTheme.colourScheme.textAndIcons.linkSecondary
            )
        }
    }, containerColor = GovUkTheme.colourScheme.surfaces.alert
    )
}

@Preview(showBackground = true)
@Composable
private fun MessagesDetailLoadingPreview() {
    GovUkTheme {
        MessagesDetailScreen(
            {},
            MessagesDetailUiState.Loading,
            MessagesDetailActions({}, {}, {}, {}, {}, {}),
            false
        )

    }
}

@Preview(showBackground = true)
@Composable
private fun MessagesDetailErrorPreview() {
    GovUkTheme {
        MessagesDetailScreen(
            {},
            MessagesDetailUiState.Error,
            MessagesDetailActions({}, {}, {}, {}, {}, {}),
            false
        )

    }
}

@Preview(showBackground = true)
@Composable
private fun MessagesDetailLoadedPreview() {
    GovUkTheme {
        MessagesDetailScreen(
            {},
            MessagesDetailUiState.Loaded(mockNotifications.recent.first(), false),
            MessagesDetailActions({}, {}, {}, {}, {}, {}),
            false
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ConfirmationDialogPreview() {
    GovUkTheme {
        ConfirmationDialog({}, {})
    }
}