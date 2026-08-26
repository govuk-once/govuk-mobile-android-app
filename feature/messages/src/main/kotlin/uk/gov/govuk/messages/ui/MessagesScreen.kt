package uk.gov.govuk.messages.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.text
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import uk.gov.govuk.messages.data.model.Notification
import uk.gov.govuk.messages.data.model.MessageGroups
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.ChildPageHeader
import uk.gov.govuk.design.ui.component.FootnoteRegularLabel
import uk.gov.govuk.design.ui.component.LargeVerticalSpacer
import uk.gov.govuk.design.ui.component.Title
import uk.gov.govuk.design.ui.component.Title3SemiBoldLabel
import uk.gov.govuk.design.ui.model.HeaderDismissStyle
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.messages.MessagesUiState
import uk.gov.govuk.messages.MessagesViewModel
import uk.gov.govuk.messages.R
import uk.gov.govuk.messages.data.model.NotificationFixtures.Companion.mockNotifications

@Composable
internal fun MessagesRoute(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    onTapNotification: (Notification) -> Unit) {
    val viewModel: MessagesViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier
            .fillMaxSize()
            .background(GovUkTheme.colourScheme.surfaces.screenBackground)
    ) {
        MessagesScreen(
            {
                viewModel.onPageView()
            },
            uiState,
            onBack,
            onTapNotification
        )
    }
}

@Composable
private fun MessagesScreen(
    onPageView: () -> Unit,
    state: MessagesUiState,
    onBack: () -> Unit,
    onTapNotification: (Notification) -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(GovUkTheme.colourScheme.surfaces.chatBackground),
        Arrangement.Center,
        Alignment.CenterHorizontally
    ) {
        ChildPageHeader(
            dismissStyle = HeaderDismissStyle.Back(onBack)
        )

        Title(
            stringResource(R.string.messages_title)
        )

        when (state) {
            is MessagesUiState.Loading -> MessagesScreenLoading()
            is MessagesUiState.Empty -> MessagesScreenEmpty()
            is MessagesUiState.Error -> MessagesScreenError()
            is MessagesUiState.NoInternet -> MessagesScreenNoInternet()
            is MessagesUiState.Loaded -> MessagesScreenLoaded(
                state.notifications,
                onTapNotification,
            )
            else -> {}
        }
        LaunchedEffect(Unit) {
            onPageView()
        }
    }
}

@Composable
private fun MessagesScreenLoading() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val loadingContentDescription = stringResource(R.string.loading_content_description)
        CircularProgressIndicator(
            Modifier
                .size(36.dp)
                .semantics {
                    text = AnnotatedString(loadingContentDescription)
                },
            GovUkTheme.colourScheme.surfaces.primary
        )
    }
}

@Composable
private fun MessagesScreenEmpty() {
    Column(
        Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(GovUkTheme.colourScheme.surfaces.cardNonTappable)
        ) {
            BodyRegularLabel(
                stringResource(R.string.empty_body),
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                GovUkTheme.colourScheme.textAndIcons.secondary,
                TextAlign.Center
            )
        }

        Footer()
    }
}



@Composable
private fun MessagesScreenLoaded(
    notifications: MessageGroups,
    onTapNotification: (Notification) -> Unit
) {
    LazyColumn(
        Modifier
            .padding(horizontal = GovUkTheme.spacing.medium)
    ) {
        item {
            LargeVerticalSpacer()
        }

        if (notifications.recent.isNotEmpty()) {
            item {
                NotificationSectionHeader(stringResource(R.string.section_recent))
            }

            items(notifications.recent) { not ->
                NotificationRow(not, onTapRow = { onTapNotification(it) })
            }
        }

        if (notifications.older.isNotEmpty()) {
            item {
                NotificationSectionHeader(stringResource(R.string.section_older))
            }

            items(notifications.older) { not ->
                NotificationRow(
                    not
                ) {
                    onTapNotification(it)
                }
            }
        }

        item {
            Footer()
        }
    }
}

@Composable
private fun NotificationSectionHeader(title: String) {
    Title3SemiBoldLabel(
        title,
        Modifier.padding(top = 28.dp, bottom = 16.dp)
    )
}

@Composable
private fun NotificationRow(
    message: Notification,
    onTapRow: (Notification) -> Unit
) {
    val unreadContentDescription = stringResource(R.string.unread_content_description)
    val titleStyle: TextStyle
    val subtitleStyle: TextStyle
    val indicatorColor: Color

    if (message.isUnread) {
        titleStyle = GovUkTheme.typography.bodyBold
        subtitleStyle = GovUkTheme.typography.subheadlineBold
        indicatorColor = GovUkTheme.colourScheme.surfaces.msgUnread
    } else {
        titleStyle = GovUkTheme.typography.bodyRegular
        subtitleStyle = GovUkTheme.typography.subheadlineRegular
        indicatorColor = GovUkTheme.colourScheme.surfaces.msgRead

    }

    Row(
        Modifier
            .padding(bottom = 8.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(GovUkTheme.colourScheme.surfaces.list)
            .clickable {  onTapRow(message) }
            .semantics(mergeDescendants = true) {
                role = Role.Button
            }, 
            verticalAlignment = Alignment.CenterVertically
    ) {

        // zIndex modifier is used to set the order the views are
        // ready by the screen-reader.
        // Feels like a hack, but it's in the docs
        // https://developer.android.com/develop/ui/compose/accessibility/traversal#api-considerations
        Box(
            Modifier
                .padding(horizontal = 16.dp)
                .clip(CircleShape)
                .background(indicatorColor)
                .size(10.dp)
                .zIndex(1F)
                .semantics {
                    if (message.isUnread) {
                        text = AnnotatedString(unreadContentDescription)
                    } else {
                        hideFromAccessibility()
                    }
                }
        )
        Column(
            Modifier
                .padding(vertical = 16.dp)
                .padding(end = 16.dp)
                .zIndex(0F)
        ) {
            Text(
                message.title,
                Modifier.padding(bottom = 4.dp),
                GovUkTheme.colourScheme.textAndIcons.primary,
                style = titleStyle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                message.formattedDate,
                color = GovUkTheme.colourScheme.textAndIcons.secondary,
                style = subtitleStyle ,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MessagesLoadingPreview() {
    GovUkTheme {
        MessagesScreen({}, MessagesUiState.Loading, {}) { }

    }
}

@Preview(showBackground = true)
@Composable
private fun MessagesEmptyPreview() {
    GovUkTheme {
        MessagesScreen({}, MessagesUiState.Empty, { }) { }
    }
}

@Preview(showBackground = true)
@Composable
private fun MessagesLoadedPreview() {
    GovUkTheme {
        MessagesScreen(
            {},
            MessagesUiState.Loaded(mockNotifications),
            { }
        ) { }

    }
}

@Preview(showBackground = true)
@Composable
private fun NotificationRowReadPreview() {
    GovUkTheme {
        NotificationRow(mockNotifications.recent[2]) { }
    }
}

@Preview(showBackground = true)
@Composable
private fun NotificationRowUnreadPreview() {
    GovUkTheme {
        NotificationRow(mockNotifications.recent[3]) { }
    }
}