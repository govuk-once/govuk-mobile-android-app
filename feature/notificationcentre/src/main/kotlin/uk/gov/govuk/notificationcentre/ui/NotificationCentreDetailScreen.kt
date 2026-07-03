package uk.gov.govuk.notificationcentre.ui

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.text
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mikepenz.markdown.m3.Markdown
import com.mikepenz.markdown.m3.markdownTypography
import uk.gov.govuk.notificationcentre.data.model.Notification
import uk.gov.govuk.design.ui.component.BodyBoldLabel
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.DestructiveButton
import uk.gov.govuk.design.ui.component.SecondaryButton
import uk.gov.govuk.design.ui.component.Title1BoldLabel
import uk.gov.govuk.design.ui.component.Title2BoldLabel
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.notificationcentre.NotificationCentreDetailUiState
import uk.gov.govuk.notificationcentre.NotificationCentreDetailViewModel
import uk.gov.govuk.notificationcentre.R

@Composable
internal fun NotificationCentreDetailRoute(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    launchBrowser: (url: String) -> Unit
) {
    val viewModel: NotificationCentreDetailViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier
            .fillMaxSize()
            .background(GovUkTheme.colourScheme.surfaces.fullScreen)
    ) {
        NotificationCentreDetailScreen(
            {
                viewModel.onPageView()
            },
            uiState,
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
            },
            showDeleteConfirmation = (uiState as? NotificationCentreDetailUiState.Loaded)?.showDeleteConfirmation ?: false
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotificationCentreDetailScreen(
    onPageView: () -> Unit,
    state: NotificationCentreDetailUiState,
    onBack: () -> Unit,
    onUnread: () -> Unit,
    onTapDelete: () -> Unit,
    onCancelDelete: () -> Unit,
    onConfirmDelete: () -> Unit,
    launchBrowser: (url: String) -> Unit,
    showDeleteConfirmation: Boolean
) {
    val deleteBottomSheetState = rememberModalBottomSheetState()
    var showDeleteBottomSheet by remember { mutableStateOf(false) }

    LaunchedEffect(showDeleteConfirmation) {
        showDeleteBottomSheet = showDeleteConfirmation
    }

    Column(
        Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Header(
            onBack = onBack,
            onUnread = onUnread,
            onDelete = {
                onTapDelete()
            }
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            when (state) {
                is NotificationCentreDetailUiState.Loading -> NotificationCentreDetailScreenLoading()
                is NotificationCentreDetailUiState.NoInternet -> NotificationCentreScreenNoInternet()
                is NotificationCentreDetailUiState.Error -> NotificationCentreScreenError()
                is NotificationCentreDetailUiState.Loaded -> NotificationCentreDetailScreenLoaded(
                    state.notification,
                    launchBrowser
                )
                else -> {}
            }
        }

        if (showDeleteBottomSheet) {
            ModalBottomSheet(
                sheetState = deleteBottomSheetState,
                onDismissRequest = {
                    onCancelDelete()
                }, dragHandle = null
            ) {
                ConfirmationSheet(
                    onConfirm = {
                        onConfirmDelete()
                    },
                    onCancel = {
                        onCancelDelete()
                    }
                )
            }
        }

        LaunchedEffect(Unit) {
            onPageView()
        }
    }
}

@Composable
private fun Header(
    modifier: Modifier = Modifier,
    actionColour: Color = GovUkTheme.colourScheme.textAndIcons.linkHeader,
    onBack: () -> Unit,
    onUnread: () -> Unit,
    onDelete: () -> Unit
) {
    Column(
        modifier
            .background(GovUkTheme.colourScheme.surfaces.homeHeader)
            .semantics { this.hideFromAccessibility() }
    ) {
        Row(
            modifier = Modifier
                .height(64.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(
                onClick = onBack
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(uk.gov.govuk.design.R.string.content_desc_back),
                    tint = actionColour
                )
            }

            Spacer(Modifier.weight(1f))

            IconButton(
                onClick = onUnread,
                modifier = Modifier
                    .size(48.dp)

            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_mark_unread),
                    tint = actionColour,
                    contentDescription = stringResource(R.string.mark_as_unread),
                )
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .size(48.dp)

            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_delete_notification),
                    tint = actionColour,
                    contentDescription = stringResource(R.string.delete_notification),
                )
            }
        }
    }
}

@Composable
private fun NotificationCentreDetailScreenLoading() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
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
private fun NotificationCentreDetailScreenLoaded(
    notification: Notification,
    launchBrowser: (url: String) -> Unit
) {
        val headerContentDescription = stringResource(
            R.string.notification_detail_header_content_description,
            notification.detailFormattedDate,
            notification.metadata.sender.displayName)

        Column(
            modifier = Modifier
                .padding(
                    horizontal = GovUkTheme.spacing.medium,
                    vertical = GovUkTheme.spacing.medium
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
                    }
            ) {
                BodyRegularLabel(
                    notification.detailFormattedDate,
                    color = GovUkTheme.colourScheme.textAndIcons.secondary,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                BodyBoldLabel(
                    notification.metadata.sender.displayName,
                    color = GovUkTheme.colourScheme.textAndIcons.primary,
                )
            }

            Column(modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 16.dp)) {

                Title1BoldLabel(
                    notification.messageTitle ?: notification.title,
                    color = GovUkTheme.colourScheme.textAndIcons.primary,
                    modifier = Modifier.padding(top = 24.dp, bottom = 24.dp)
                )
                
                CompositionLocalProvider(
                    LocalUriHandler provides object : UriHandler {
                        override fun openUri(uri: String) {
                            launchBrowser(uri)
                        }
                    }
                ) {
                    Markdown(
                        notification.messageBody ?: notification.body,
                        typography = govUkMarkdownTypography(),
                    )
                }
            }
    }
}

@Composable
private fun govUkMarkdownTypography() = markdownTypography(
    // Body text — Black in light, White in dark
    text = GovUkTheme.typography.bodyRegular.copy(
        color = GovUkTheme.colourScheme.textAndIcons.primary
    ),

    // H1 — Large Title: Transport Bold, 34sp / 41sp line height
    h1 = GovUkTheme.typography.titleLargeBold.copy(
        color = GovUkTheme.colourScheme.textAndIcons.primary
    ),

    // H2 — Title 1: Transport Bold, 28sp / 34sp line height
    h2 = GovUkTheme.typography.title1Bold.copy(
        color = GovUkTheme.colourScheme.textAndIcons.primary
    ),

    // H3 — Title 2: Transport Bold, 22sp / 28sp line height
    h3 = GovUkTheme.typography.title2Bold.copy(
        color = GovUkTheme.colourScheme.textAndIcons.primary
    ),

    // H4 — Title 3: Transport Bold, 20sp / 24sp line height
    h4 = GovUkTheme.typography.title3Bold.copy(
        color = GovUkTheme.colourScheme.textAndIcons.primary
    ),

    // H5 — Subheadline Bold, 15sp / 20sp line height
    h5 = GovUkTheme.typography.subheadlineBold.copy(
        color = GovUkTheme.colourScheme.textAndIcons.primary
    ),

    // H6 — Caption Bold, 12sp / 17sp line height
    h6 = GovUkTheme.typography.captionBold.copy(
        color = GovUkTheme.colourScheme.textAndIcons.primary
    ),

    // Body paragraph — Transport Light, 17sp / 22sp line height
    paragraph = GovUkTheme.typography.bodyRegular.copy(
        color = GovUkTheme.colourScheme.textAndIcons.primary
    ),

    // Ordered and unordered lists
    ordered = GovUkTheme.typography.bodyRegular.copy(
        color = GovUkTheme.colourScheme.textAndIcons.primary
    ),
    bullet = GovUkTheme.typography.bodyRegular.copy(
        color = GovUkTheme.colourScheme.textAndIcons.primary
    ),
    list = GovUkTheme.typography.bodyRegular.copy(
        color = GovUkTheme.colourScheme.textAndIcons.primary
    ),

    // Code — Footnote size (14sp), monospace override applied by the library
    code = GovUkTheme.typography.footnoteRegular.copy(
        color = GovUkTheme.colourScheme.textAndIcons.primary
    ),

    // Blockquote — body size, italic
    quote = GovUkTheme.typography.bodyRegular.copy(
        color = GovUkTheme.colourScheme.textAndIcons.primary,
        fontStyle = FontStyle.Italic
    ),

    // Links — BluePrimary (#1D70B8) in light, White in dark, with underline
    link = GovUkTheme.typography.bodyRegular.copy(
        color = GovUkTheme.colourScheme.textAndIcons.linkPrimary,
        textDecoration = TextDecoration.Underline
    ),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConfirmationSheet(onConfirm: () -> Unit, onCancel: () -> Unit) {
    Column(
        Modifier
            .background(GovUkTheme.colourScheme.surfaces.cardDefault)
            .padding(16.dp)
    ) {
        Title2BoldLabel(
            stringResource(R.string.delete_notification_sheet_title),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 16.dp)
        )

        BodyRegularLabel(
            stringResource(R.string.delete_notification_sheet_body),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
            color = GovUkTheme.colourScheme.textAndIcons.secondary
        )

        DestructiveButton(
            text = stringResource(R.string.delete_notification_sheet_confirm),
            onClick = onConfirm,
            modifier = Modifier.padding(top = 32.dp)
        )

        SecondaryButton(
            text = stringResource(R.string.delete_notification_sheet_cancel),
            onClick = onCancel,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NotificationCentreDetailLoadingPreview() {
    GovUkTheme {
        NotificationCentreDetailScreen(
            {},
            NotificationCentreDetailUiState.Loading,
            {},
            {},
            {},
            {},
            {},
            {},
            false
        )

    }
}

@Preview(showBackground = true)
@Composable
private fun NotificationCentreDetailErrorPreview() {
    GovUkTheme {
        NotificationCentreDetailScreen(
            {},
            NotificationCentreDetailUiState.Error,
            {},
            {},
            {},
            {},
            {},
            {},
            false
        )

    }
}

@Preview(showBackground = true)
@Composable
private fun NotificationCentreDetailLoadedPreview() {
    GovUkTheme {
        NotificationCentreDetailScreen(
            {},
            NotificationCentreDetailUiState.Loaded(Notification.mockNotifications.recent.first(), false),
            {},
            {},
            {},
            {},
            {},
            {},
            false
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ConfirmationSheetPreview() {
    GovUkTheme {
        ConfirmationSheet(onConfirm = {}, onCancel = {})
    }
}