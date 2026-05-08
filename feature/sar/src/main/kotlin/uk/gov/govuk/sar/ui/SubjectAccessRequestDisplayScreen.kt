package uk.gov.govuk.sar.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import uk.gov.govuk.data.user.model.ConsentStatus
import uk.gov.govuk.data.user.model.Notifications
import uk.gov.govuk.data.user.model.User
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.LargeVerticalSpacer
import uk.gov.govuk.design.ui.component.ModalHeader
import uk.gov.govuk.design.ui.model.HeaderActionStyle
import uk.gov.govuk.design.ui.model.HeaderDismissStyle
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.sar.R
import uk.gov.govuk.sar.SubjectAccessRequestViewModel

@Composable
internal fun SubjectAccessRequestDisplayRoute(
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: SubjectAccessRequestViewModel = hiltViewModel()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()

    SubjectAccessRequestDisplayScreen(
        userProfile = userProfile,
        onPageView = {
            viewModel.onDisplayPageView()
            viewModel.loadUserData()
        },
        onClose = { text ->
            viewModel.onButtonClick(text)
            onClose()
        },
        modifier = modifier
    )
}

@Composable
private fun SubjectAccessRequestDisplayScreen(
    userProfile: User?,
    onPageView: () -> Unit,
    onClose: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        onPageView()
    }

    Scaffold(
        containerColor = GovUkTheme.colourScheme.surfaces.background,
        modifier = modifier.fillMaxWidth(),

        topBar = {
            val titleText = stringResource(R.string.sar_display_title)
            ModalHeader(
                text = titleText,
                dismissStyle = HeaderDismissStyle.Close {
                    onClose(titleText)
                },
                actionStyle = HeaderActionStyle.ActionButton(
                    title = stringResource(R.string.share),
                    onClick = {}
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = GovUkTheme.spacing.medium)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            LargeVerticalSpacer()
            BodyRegularLabel(
                text = stringResource(R.string.sar_display_content),
                color = GovUkTheme.colourScheme.textAndIcons.primary,
                textAlign = TextAlign.Left,
                modifier = Modifier.semantics { heading() }
            )

            LargeVerticalSpacer()
            Row(
                modifier = Modifier
                    .padding(vertical = GovUkTheme.spacing.small)
                    .fillMaxWidth()
            ) {
                BodyRegularLabel(
                    text = stringResource(R.string.sar_display_notification_field),
                    color = GovUkTheme.colourScheme.textAndIcons.primary,
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .weight(1f),
                    textAlign = TextAlign.Left
                )

                if (userProfile != null) {
                    val consentStatus = humanize(userProfile)
                    BodyRegularLabel(
                        text = consentStatus,
                        color = GovUkTheme.colourScheme.textAndIcons.primary,
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .weight(1f),
                        textAlign = TextAlign.Right
                    )
                }
            }

            Row(
                modifier = Modifier
                    .padding(vertical = GovUkTheme.spacing.small)
                    .fillMaxWidth()
            ) {
                BodyRegularLabel(
                    text = stringResource(R.string.sar_display_push_id_field),
                    color = GovUkTheme.colourScheme.textAndIcons.primary,
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .weight(1f),
                    textAlign = TextAlign.Left
                )

                BodyRegularLabel(
                    text = userProfile?.notifications?.pushId ?: "",
                    color = GovUkTheme.colourScheme.textAndIcons.primary,
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .weight(1f),
                    textAlign = TextAlign.Right
                )
            }
        }
    }
}

private fun humanize(user: User): String {
    return user.notifications.consentStatus.toString().lowercase().replaceFirstChar { it.uppercase() }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
private fun LightModePreview() {
    GovUkTheme {
        SubjectAccessRequestDisplayScreen(
            userProfile = User(
                Notifications(
                    consentStatus = ConsentStatus.ACCEPTED,
                    pushId = "1234"
                )
            ),
            onPageView = {},
            onClose = {}
        )
    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun DarkModePreview() {
    GovUkTheme {
        SubjectAccessRequestDisplayScreen(
            userProfile = User(
                Notifications(
                    consentStatus = ConsentStatus.ACCEPTED,
                    pushId = "1234"
                )
            ),
            onPageView = {},
            onClose = {}
        )
    }
}
