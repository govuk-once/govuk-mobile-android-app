package uk.gov.govuk.notificationcentre.ui

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.ChildPageHeader
import uk.gov.govuk.design.ui.component.FootnoteRegularLabel
import uk.gov.govuk.design.ui.component.LargeVerticalSpacer
import uk.gov.govuk.design.ui.component.PrimaryButton
import uk.gov.govuk.design.ui.component.Title
import uk.gov.govuk.design.ui.component.Title1BoldLabel
import uk.gov.govuk.design.ui.model.HeaderDismissStyle
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.data.notificationcentre.model.Notification
import uk.gov.govuk.notificationcentre.NotificationCentreUiState
import uk.gov.govuk.notificationcentre.NotificationCentreViewModel
import uk.gov.govuk.notificationcentre.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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
            onTapRetry = viewModel::onTapRetry,
            onTapNotification = onTapNotification
        )
    }
}

@Composable
private fun NotificationCentreScreen(
    onPageView: () -> Unit,
    state: NotificationCentreUiState,
    onBack: () -> Unit,
    onTapRetry: () -> Unit,
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
            is NotificationCentreUiState.Error -> NotificationCentreScreenError(onTapRetry)
            is NotificationCentreUiState.Loaded -> NotificationCentreScreenLoaded(
                state.notifications,
                onTapNotification
            )
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
private fun NotificationCentreScreenError(onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            Icons.Filled.ErrorOutline,
            modifier = Modifier
                .size(64.dp)
                .padding(bottom = 16.dp)
                .semantics { hideFromAccessibility() },
            contentDescription = stringResource(R.string.error_icon_content_description),
            colorFilter = ColorFilter.tint(GovUkTheme.colourScheme.surfaces.cardEmergencyLocal)
        )

        Column(modifier = Modifier.semantics(true) {}) {
            Title1BoldLabel(
                stringResource(R.string.error_title),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            BodyRegularLabel(
                stringResource(R.string.error_body),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                color = GovUkTheme.colourScheme.textAndIcons.secondary
            )
        }

        PrimaryButton(
            text = stringResource(R.string.error_button),
            onClick = { onRetry() },
            modifier = Modifier.padding(top = 32.dp)
        )
    }
}

@Composable
private fun NotificationCentreScreenEmpty() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = uk.gov.govuk.design.R.drawable.ic_notcenbell),
            modifier = Modifier
                .size(64.dp)
                .padding(bottom = 16.dp)
                .semantics { hideFromAccessibility() },
            contentDescription = stringResource(R.string.empty_icon_content_description),
            colorFilter = ColorFilter.tint(GovUkTheme.colourScheme.surfaces.cardEmergencyLocal)
        )

        Column(modifier = Modifier.semantics(true) {}) {

            Title1BoldLabel(
                stringResource(R.string.empty_title),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            BodyRegularLabel(
                stringResource(R.string.empty_body),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                color = GovUkTheme.colourScheme.textAndIcons.secondary
            )
        }
    }
}

@Composable
private fun NotificationCentreScreenLoaded(
    notifications: List<Notification>,
    onTapNotification: (Notification) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .padding(horizontal = GovUkTheme.spacing.medium)
    ) {
        item {
            LargeVerticalSpacer()
        }

        itemsIndexed(notifications) { index, not ->
            val isFirst = index == 0
            val isLast = index == notifications.size - 1

            NotificationRow(not, isFirst, isLast, onTapRow = { onTapNotification(it) })
            if (index < notifications.size - 1) {
                HorizontalDivider(
                    thickness = 1.dp,
                    modifier =
                        Modifier
                            .background(GovUkTheme.colourScheme.surfaces.list)
                            .padding(start = 24.dp, end = 16.dp)
                            .padding(vertical = 1.dp)
                            .alpha(0.5f), // Makes it a bit less harsh and closer to iOS
                    color = GovUkTheme.colourScheme.textAndIcons.secondary
                )
            }
        }

        item {
            LargeVerticalSpacer()
        }
    }
}

@Composable
private fun NotificationRow(
    notification: Notification,
    isFirst: Boolean,
    isLast: Boolean,
    onTapRow: (Notification) -> Unit
) {
    val indicatorColour = GovUkTheme.colourScheme.surfaces.listSelected
    val cornerRadius = GovUkTheme.numbers.cornerAndroidList
    val unreadContentDescription = stringResource(R.string.unread_content_description)
    Row(
        Modifier
            .clip(
                RoundedCornerShape(
                    topStart = if (isFirst) cornerRadius else 0.dp,
                    topEnd = if (isFirst) cornerRadius else 0.dp,
                    bottomStart = if (isLast) cornerRadius else 0.dp,
                    bottomEnd = if (isLast) cornerRadius else 0.dp
                )
            )
            .background(GovUkTheme.colourScheme.surfaces.list)
            .drawBehind {
                if (notification.isUnread) {
                    drawRect(
                        color = indicatorColour, size = Size(8.dp.toPx(), size.height)
                    )
                }
            }

            .clickable {  onTapRow(notification) }
            .semantics(mergeDescendants = true) {}, verticalAlignment = Alignment.CenterVertically
    ) {

        // Account for the unread indicator by adding an extra 8dp to start padding
        Column(
            modifier = Modifier
                .padding(vertical = 16.dp)
                .padding(start = 24.dp, end = 16.dp)
                .weight(1.0f)
                .semantics(mergeDescendants = true) {
                    if (notification.isUnread) {
                        text = AnnotatedString(unreadContentDescription)
                    }
                    role = Role.Button
                }
        ) {
            LineLimitedLabel(
                notification.title,
                color = GovUkTheme.colourScheme.textAndIcons.primary,
                maxLines = 2,
                style = GovUkTheme.typography.headlineSemibold,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            LineLimitedLabel(
                notification.body,
                color = GovUkTheme.colourScheme.textAndIcons.secondary,
                maxLines = 3,
                style = GovUkTheme.typography.subheadlineRegular,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            FootnoteRegularLabel(
                stringResource(R.string.sent_date_format, formatDate(notification.date)) ,
                color = GovUkTheme.colourScheme.textAndIcons.secondary
            )
        }

        Icon(
            painter = painterResource(uk.gov.govuk.design.R.drawable.ic_arrow),
            contentDescription = null,
            tint = GovUkTheme.colourScheme.surfaces.listSelected,
            modifier = Modifier
                .padding(end = 16.dp)
                .semantics { hideFromAccessibility() }
        )
    }
}

@Composable
fun LineLimitedLabel(
    text: String,
    modifier: Modifier = Modifier,
    color: Color,
    maxLines: Int,
    style: TextStyle
) {
    Text(
        text = text,
        modifier = modifier,
        style = style,
        color = color,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis
    )
}

private fun formatDate(date: LocalDateTime): String =
    DateTimeFormatter.ofPattern("d MMM yyyy, h:mma").format(date)

@Preview(showBackground = true)
@Composable
private fun NotificationCentreLoadingPreview() {
    GovUkTheme {
        NotificationCentreScreen({}, NotificationCentreUiState.Loading, {},{ }) { }

    }
}

@Preview(showBackground = true)
@Composable
private fun NotificationCentreErrorPreview() {
    GovUkTheme {
        NotificationCentreScreen({}, NotificationCentreUiState.Error, { }, {}) { }

    }
}

@Preview(showBackground = true)
@Composable
private fun NotificationCentreEmptyPreview() {
    GovUkTheme {
        NotificationCentreScreen({}, NotificationCentreUiState.Empty, { }, {}) { }
    }
}

@Preview(showBackground = true)
@Composable
private fun NotificationCentreLoadedPreview() {
    GovUkTheme {
        NotificationCentreScreen(
            {},
            NotificationCentreUiState.Loaded(Notification.mockNotifications),
            { },
            {}
        ) { }

    }
}

@Preview(showBackground = true)
@Composable
private fun NotificationRowReadPreview() {
    GovUkTheme {
        NotificationRow(Notification.mockNotifications[2], true, isLast = true) { }
    }
}

@Preview(showBackground = true)
@Composable
private fun NotificationRowUnreadPreview() {
    GovUkTheme {
        NotificationRow(Notification.mockNotifications[3], true, isLast = true) { }
    }
}