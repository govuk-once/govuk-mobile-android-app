package uk.gov.govuk.notificationcentre.ui

import android.text.SpannableString
import android.text.style.URLSpan
import android.text.util.Linkify
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.text
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import uk.gov.govuk.data.notificationcentre.model.Notification
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.CalloutRegularLabel
import uk.gov.govuk.design.ui.component.PrimaryButton
import uk.gov.govuk.design.ui.component.Title
import uk.gov.govuk.design.ui.component.Title1BoldLabel
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.notificationcentre.NotificationCentreDetailUiState
import uk.gov.govuk.notificationcentre.NotificationCentreDetailViewModel
import uk.gov.govuk.notificationcentre.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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
            .background(GovUkTheme.colourScheme.surfaces.screenBackground)
    ) {
        NotificationCentreDetailScreen(
            {
                viewModel.onPageView()
            }, uiState,
            onBack = onBack,
            onUnread = {
                viewModel.onTapMarkUnread()
                onBack()
            },
            onTapRetry = viewModel::onTapRetry,
            launchBrowser = {
                launchBrowser(it)
                viewModel.onLinkTap(it)
            }
        )
    }
}

@Composable
private fun NotificationCentreDetailScreen(
    onPageView: () -> Unit,
    state: NotificationCentreDetailUiState,
    onBack: () -> Unit,
    onUnread: () -> Unit,
    onTapRetry: () -> Unit,
    launchBrowser: (url: String) -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(GovUkTheme.colourScheme.surfaces.chatBackground),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Header(
            onBack = onBack,
            onUnread = onUnread,
            onDelete = {}
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Title(
                stringResource(R.string.notification_centre_detail_title)
            )

            when (state) {
                is NotificationCentreDetailUiState.Loading -> NotificationCentreDetailScreenLoading()
                is NotificationCentreDetailUiState.NotFound -> NotificationCentreDetailScreenNotFound()
                is NotificationCentreDetailUiState.Error -> NotificationCentreDetailScreenError(
                    onTapRetry
                )

                is NotificationCentreDetailUiState.Loaded -> NotificationCentreDetailScreenLoaded(
                    state.notification,
                    launchBrowser
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
    actionColour : Color = GovUkTheme.colourScheme.textAndIcons.linkHeader,
    onBack: () -> Unit,
    onUnread: () -> Unit,
    onDelete: () -> Unit
) {
    Column(
        modifier
            .background( GovUkTheme.colourScheme.surfaces.homeHeader)
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

//            IconButton(
//                onClick = onDelete,
//                modifier = Modifier
//                    .size(48.dp)
//
//            ) {
//                Icon(
//                    painter = painterResource(id = R.drawable.ic_delete_notification),
//                    tint = actionColour,
//                    contentDescription = stringResource(R.string.delete_notification),
//                )
//            }
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
private fun NotificationCentreDetailScreenError(onRetry: () -> Unit) {
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
                stringResource(R.string.notification_detail_error_body),
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
private fun NotificationCentreDetailScreenNotFound() {
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
                stringResource(R.string.notification_not_found_title),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            BodyRegularLabel(
                stringResource(R.string.notification_not_found_body),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                color = GovUkTheme.colourScheme.textAndIcons.secondary
            )
        }
    }
}

@Composable
private fun NotificationCentreDetailScreenLoaded(
    notification: Notification,
    launchBrowser: (url: String) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(horizontal = GovUkTheme.spacing.medium, vertical = GovUkTheme.spacing.medium)
            .fillMaxSize()
            .clip(RoundedCornerShape(10.dp))
            .background(GovUkTheme.colourScheme.surfaces.cardEmergencyInformation)
    ) {
        Column(modifier = Modifier.padding(horizontal = GovUkTheme.spacing.medium, vertical = GovUkTheme.spacing.medium)
        ) {
            Title1BoldLabel(
                notification.messageTitle ?: notification.title,
                color = GovUkTheme.colourScheme.textAndIcons.primary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            CalloutRegularLabel(
                stringResource(R.string.sent_date_format, formatDate(notification.date)),
                color = GovUkTheme.colourScheme.textAndIcons.secondary,
                modifier = Modifier.padding(bottom = 32.dp)

            )

            LinkifyText(notification.messageBody ?: notification.title, onClick = {
                launchBrowser(it)
            })
        }
    }
}

@Composable
fun LinkifyText(text: String, onClick: (String) -> Unit) {
    val linkColour = GovUkTheme.colourScheme.textAndIcons.link
    val annotatedString = remember(text) {
        val spanned = SpannableString(text)
        Linkify.addLinks(spanned, Linkify.WEB_URLS)

        buildAnnotatedString {
            append(text)

            // Apply link annotations from Linkify
            spanned.getSpans(0, spanned.length, URLSpan::class.java).forEach { span ->
                val start = spanned.getSpanStart(span)
                val end = spanned.getSpanEnd(span)

                addStyle(
                    style = SpanStyle(
                        color = linkColour,
                        textDecoration = TextDecoration.Underline
                    ),
                    start = start,
                    end = end
                )
                addLink(
                    url = LinkAnnotation.Url(
                        url = span.url,
                        styles = TextLinkStyles(
                            style = SpanStyle(
                                color = linkColour,
                                textDecoration = TextDecoration.Underline
                            )
                        ),
                        linkInteractionListener = { _ -> onClick(span.url)}
                    ),
                    start = start,
                    end = end
                )
            }
        }
    }

    Text(
       annotatedString,
        color = GovUkTheme.colourScheme.textAndIcons.primary,
        style = GovUkTheme.typography.bodyRegular,
    )
}



private fun formatDate(date: LocalDateTime): String =
    DateTimeFormatter.ofPattern("d MMM yyyy, h:mma").format(date)

@Preview(showBackground = true)
@Composable
private fun NotificationCentreDetailLoadingPreview() {
    GovUkTheme {
        NotificationCentreDetailScreen({}, NotificationCentreDetailUiState.Loading, {},{}, {}, launchBrowser = {})

    }
}

@Preview(showBackground = true)
@Composable
private fun NotificationCentreDetailErrorPreview() {
    GovUkTheme {
        NotificationCentreDetailScreen({}, NotificationCentreDetailUiState.Error, { }, {}, {}, launchBrowser = {})

    }
}

@Preview(showBackground = true)
@Composable
private fun NotificationCentreDetailNotFoundPreview() {
    GovUkTheme {
        NotificationCentreDetailScreen({}, NotificationCentreDetailUiState.NotFound, { }, {}, {}, launchBrowser = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NotificationCentreDetailLoadedPreview() {
    GovUkTheme {
        NotificationCentreDetailScreen(
            {},
            NotificationCentreDetailUiState.Loaded(Notification.mockNotifications.first()),
            { },
            {},
            {},
            launchBrowser = {}
        )
    }
}