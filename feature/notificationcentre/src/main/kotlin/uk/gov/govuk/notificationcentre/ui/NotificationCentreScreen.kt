package uk.gov.govuk.notificationcentre.ui

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.text
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import uk.gov.govuk.notificationcentre.data.model.Notification
import uk.gov.govuk.notificationcentre.data.model.NotificationGroups
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.ChildPageHeader
import uk.gov.govuk.design.ui.component.FootnoteRegularLabel
import uk.gov.govuk.design.ui.component.LargeVerticalSpacer
import uk.gov.govuk.design.ui.component.Title
import uk.gov.govuk.design.ui.component.Title3SemiBoldLabel
import uk.gov.govuk.design.ui.model.HeaderDismissStyle
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.notificationcentre.NotificationCentreUiState
import uk.gov.govuk.notificationcentre.NotificationCentreViewModel
import uk.gov.govuk.notificationcentre.R

@Composable
internal fun NotificationCentreRoute(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    onTapNotification: (Notification) -> Unit) {
    val viewModel: NotificationCentreViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier
            .fillMaxSize()
            .background(GovUkTheme.colourScheme.surfaces.screenBackground)
    ) {
        NotificationCentreScreen(
            {
                viewModel.onPageView()
            }, uiState,
            onBack = onBack,
            onTapNotification = onTapNotification
        )
    }
}

@Composable
private fun NotificationCentreScreen(
    onPageView: () -> Unit,
    state: NotificationCentreUiState,
    onBack: () -> Unit,
    onTapNotification: (Notification) -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(GovUkTheme.colourScheme.surfaces.chatBackground),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ChildPageHeader(
            dismissStyle = HeaderDismissStyle.Back(onBack)
        )

        Title(
            title = stringResource(R.string.notification_centre_title)
        )

        when (state) {
            is NotificationCentreUiState.Loading -> NotificationCentreScreenLoading()
            is NotificationCentreUiState.Empty -> NotificationCentreScreenEmpty()
            is NotificationCentreUiState.Error -> NotificationCentreScreenError()
            is NotificationCentreUiState.NoInternet -> NotificationCentreScreenNoInternet()
            is NotificationCentreUiState.Loaded -> NotificationCentreScreenLoaded(
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
private fun NotificationCentreScreenLoading() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val loadingContentDescription = stringResource(R.string.loading_content_description)
        CircularProgressIndicator(
            modifier = Modifier.size(36.dp).semantics {
                text = AnnotatedString(loadingContentDescription)
            },
            color = GovUkTheme.colourScheme.surfaces.primary,
        )
    }
}

@Composable
private fun NotificationCentreScreenEmpty() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(GovUkTheme.colourScheme.surfaces.cardNonTappable)
        ) {
            BodyRegularLabel(
                stringResource(R.string.empty_body),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                color = GovUkTheme.colourScheme.textAndIcons.secondary
            )
        }

        Footer()
    }
}



@Composable
private fun NotificationCentreScreenLoaded(
    notifications: NotificationGroups,
    onTapNotification: (Notification) -> Unit
) {
    LazyColumn(
        modifier = Modifier
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
                NotificationRow(not, onTapRow = { onTapNotification(it) })
            }
        }

        item {
            Footer()
        }
    }
}

@Composable
private fun NotificationSectionHeader(title: String) {
    Title3SemiBoldLabel(title, modifier = Modifier.padding(top = 28.dp, bottom = 16.dp))
}

@Composable
private fun NotificationRow(
    notification: Notification,
    onTapRow: (Notification) -> Unit
) {
    val unreadContentDescription = stringResource(R.string.unread_content_description)
    Row(
        Modifier
            .padding(bottom = 8.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(GovUkTheme.colourScheme.surfaces.list)
            .clickable {  onTapRow(notification) }
            .semantics(mergeDescendants = true) {}, verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            Modifier
                .padding(horizontal = 16.dp)
                .clip(CircleShape)
                .background(if(notification.isUnread)
                    GovUkTheme.colourScheme.surfaces.msgUnread
                else
                    GovUkTheme.colourScheme.surfaces.msgRead)
                .size(10.dp)
                .semantics {
                    hideFromAccessibility()
                }
        )
        Column(
            modifier = Modifier
                .padding(vertical = 16.dp)
                .padding(end = 16.dp)
                .semantics(mergeDescendants = true) {
                    if (notification.isUnread) {
                        text = AnnotatedString(unreadContentDescription)
                    }
                    role = Role.Button
                }
        ) {
            Text(
                text = notification.title,
                modifier = Modifier.padding(bottom = 4.dp),
                style = GovUkTheme.typography.headlineSemibold,
                color = GovUkTheme.colourScheme.textAndIcons.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            FootnoteRegularLabel(
                notification.formattedDate,
                color = GovUkTheme.colourScheme.textAndIcons.secondary
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NotificationCentreLoadingPreview() {
    GovUkTheme {
        NotificationCentreScreen({}, NotificationCentreUiState.Loading, {}) { }

    }
}

@Preview(showBackground = true)
@Composable
private fun NotificationCentreEmptyPreview() {
    GovUkTheme {
        NotificationCentreScreen({}, NotificationCentreUiState.Empty, { }) { }
    }
}

@Preview(showBackground = true)
@Composable
private fun NotificationCentreLoadedPreview() {
    GovUkTheme {
        NotificationCentreScreen(
            {},
            NotificationCentreUiState.Loaded(Notification.mockNotifications),
            { }
        ) { }

    }
}

@Preview(showBackground = true)
@Composable
private fun NotificationRowReadPreview() {
    GovUkTheme {
        NotificationRow(Notification.mockNotifications.recent[2]) { }
    }
}

@Preview(showBackground = true)
@Composable
private fun NotificationRowUnreadPreview() {
    GovUkTheme {
        NotificationRow(Notification.mockNotifications.recent[3]) { }
    }
}