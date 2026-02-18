package uk.gov.govuk.settings.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.CaptionRegularLabel
import uk.gov.govuk.design.ui.component.CaptionRegularLabelTrailingLink
import uk.gov.govuk.design.ui.component.CardListItem
import uk.gov.govuk.design.ui.component.ExternalLinkListItem
import uk.gov.govuk.design.ui.component.InternalLinkListItem
import uk.gov.govuk.design.ui.component.LargeVerticalSpacer
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.component.SmallHorizontalSpacer
import uk.gov.govuk.design.ui.component.SmallVerticalSpacer
import uk.gov.govuk.design.ui.component.SubheadlineRegularLabel
import uk.gov.govuk.design.ui.component.TabHeader
import uk.gov.govuk.design.ui.component.ToggleListItem
import uk.gov.govuk.design.ui.model.ExternalLinkListItemStyle
import uk.gov.govuk.design.ui.model.InternalLinkListItemStyle
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.settings.R
import uk.gov.govuk.settings.SettingsUiState
import uk.gov.govuk.settings.SettingsViewModel

internal class SettingsRouteActions(
    val onAccountClick: () -> Unit,
    val onSignOutClick: () -> Unit,
    val onNotificationsClick: () -> Unit,
    val onBiometricsClick: () -> Unit,
    val onPrivacyPolicyClick: () -> Unit,
    val onHelpClick: () -> Unit,
    val onAccessibilityStatementClick: () -> Unit,
    val onOpenSourceLicenseClick: () -> Unit,
    val onTermsAndConditionsClick: () -> Unit
)

@Composable
internal fun SettingsRoute(
    appVersion: String,
    actions: SettingsRouteActions,
    modifier: Modifier = Modifier
) {
    val viewModel: SettingsViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var notificationStatus by remember { mutableIntStateOf(R.string.off_button) }

    LifecycleResumeEffect(Unit) {
        val isEnabled = NotificationManagerCompat.from(context).areNotificationsEnabled()
        notificationStatus = if (isEnabled) R.string.on_button else R.string.off_button
        onPauseOrDispose { }
    }
    uiState?.let {
        SettingsScreen(
            uiState = it,
            appVersion = appVersion,
            notificationStatusResId = notificationStatus,
            actions = SettingsActions(
                onPageView = { viewModel.onPageView() },
                onAccountClick = {
                    viewModel.onAccount()
                    actions.onAccountClick()
                },
                onSignOutClick = {
                    viewModel.onSignOut()
                    actions.onSignOutClick()
                },
                onNotificationsClick = {
                    viewModel.onNotificationsClick()
                    actions.onNotificationsClick()
                },
                onBiometricsClick = { text ->
                    viewModel.onBiometricsClick(text)
                    actions.onBiometricsClick()
                },
                onAnalyticsConsentChange = { enabled -> viewModel.onAnalyticsConsentChanged(enabled) },
                onPrivacyPolicyClick = {
                    viewModel.onPrivacyPolicyView()
                    actions.onPrivacyPolicyClick()
                },
                onHelpClick = {
                    viewModel.onHelpAndFeedbackView()
                    actions.onHelpClick()
                },
                onAccessibilityStatementClick = {
                    viewModel.onAccessibilityStatementView()
                    actions.onAccessibilityStatementClick()
                },
                onLicenseClick = {
                    viewModel.onLicenseView()
                    actions.onOpenSourceLicenseClick()
                },
                onTermsAndConditionsClick = {
                    viewModel.onTermsAndConditionsView()
                    actions.onTermsAndConditionsClick()
                }
            ),
            modifier = modifier
        )
    }
}

internal class SettingsActions(
    val onPageView: () -> Unit = {},
    val onAccountClick: () -> Unit = {},
    val onSignOutClick: () -> Unit = {},
    val onNotificationsClick: () -> Unit = {},
    val onBiometricsClick: (String) -> Unit = {},
    val onAnalyticsConsentChange: (Boolean) -> Unit = {},
    val onPrivacyPolicyClick: () -> Unit = {},
    val onHelpClick: () -> Unit = {},
    val onAccessibilityStatementClick: () -> Unit = {},
    val onLicenseClick: () -> Unit = {},
    val onTermsAndConditionsClick: () -> Unit = {}
)

@Composable
internal fun SettingsScreen(
    uiState: SettingsUiState,
    appVersion: String,
    notificationStatusResId: Int,
    actions: SettingsActions,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        actions.onPageView()
    }

    Column(
        modifier = modifier.background(GovUkTheme.colourScheme.surfaces.screenBackground)
    ) {
        TabHeader(stringResource(R.string.screen_title))
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = GovUkTheme.spacing.medium)
                .padding(bottom = GovUkTheme.spacing.large)
        ) {
            MediumVerticalSpacer()

            ManageLogin(
                userEmail = uiState.userEmail,
                onAccountClick = actions.onAccountClick,
                onSignOutClick = actions.onSignOutClick
            )

            LargeVerticalSpacer()

            NotificationsAndPrivacy(
                uiState = uiState,
                notificationStatusResId = notificationStatusResId,
                actions = actions
            )

            MediumVerticalSpacer()

            AboutTheApp(
                appVersion = appVersion,
                onHelpClick = actions.onHelpClick
            )

            MediumVerticalSpacer()

            PrivacyPolicy(actions.onPrivacyPolicyClick)
            AccessibilityStatement(actions.onAccessibilityStatementClick)
            OpenSourceLicenses(actions.onLicenseClick)
            TermsAndConditions(actions.onTermsAndConditionsClick)
        }
    }
}

@Composable
private fun ManageLogin(
    userEmail: String,
    onAccountClick: () -> Unit,
    onSignOutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        CardListItem(
            isFirst = true,
            isLast = false
        ) {
            Row(
                modifier = Modifier.padding(GovUkTheme.spacing.medium),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_login),
                    contentDescription = null
                )
                SmallHorizontalSpacer()
                Column {
                    BodyRegularLabel(stringResource(R.string.manage_login_header_title))
                    SubheadlineRegularLabel(
                        text = userEmail,
                        color = GovUkTheme.colourScheme.textAndIcons.secondary
                    )
                }
            }
        }

        ExternalLinkListItem(
            title = stringResource(R.string.manage_login_link),
            onClick = onAccountClick,
            isFirst = false,
            style = ExternalLinkListItemStyle.Icon
        )

        SmallVerticalSpacer()

        CaptionRegularLabel(
            text = stringResource(R.string.manage_login_description),
            modifier = Modifier.padding(horizontal = GovUkTheme.spacing.medium)
        )

        MediumVerticalSpacer()

        CardListItem(
            modifier = Modifier.fillMaxWidth(),
            onClick = onSignOutClick
        ) {
            BodyRegularLabel(
                text = stringResource(R.string.manage_login_sign_out),
                modifier = Modifier
                    .padding(GovUkTheme.spacing.medium),
                color = GovUkTheme.colourScheme.textAndIcons.buttonDestructive
            )
        }
    }
}

@Composable
private fun NotificationsAndPrivacy(
    uiState: SettingsUiState,
    notificationStatusResId: Int,
    actions: SettingsActions,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        if (uiState.isNotificationsEnabled) {
            Notifications(
                onNotificationsClick = actions.onNotificationsClick,
                statusResId = notificationStatusResId
            )
        }

        if (uiState.isAuthenticationEnabled) {
            val biometricTitle = stringResource(R.string.biometric_title)
            InternalLinkListItem(
                title = biometricTitle,
                onClick = { actions.onBiometricsClick(biometricTitle) },
                isFirst = !uiState.isNotificationsEnabled,
                isLast = false
            )
        }

        ToggleListItem(
            title = stringResource(R.string.share_setting),
            checked = uiState.isAnalyticsEnabled,
            onCheckedChange = actions.onAnalyticsConsentChange,
            isFirst = !uiState.isAuthenticationEnabled && !uiState.isNotificationsEnabled
        )

        SmallVerticalSpacer()

        val description = stringResource(R.string.privacy_description)
        val linkText = stringResource(R.string.privacy_read_more)
        val altText = "$description $linkText ${stringResource(R.string.link_opens_in)}"

        CaptionRegularLabelTrailingLink(
            text = description,
            linkText = linkText,
            onClick = actions.onPrivacyPolicyClick,
            altText = altText,
            modifier = Modifier.padding(horizontal = GovUkTheme.spacing.medium)
        )
    }
}

@Composable
private fun Notifications(
    onNotificationsClick: () -> Unit,
    statusResId: Int,
    modifier: Modifier = Modifier
) {

    InternalLinkListItem(
        title = stringResource(R.string.notifications_title),
        onClick = onNotificationsClick,
        modifier = modifier,
        isFirst = true,
        isLast = false,
        style = InternalLinkListItemStyle.Status(stringResource(statusResId))
    )
}

@Composable
private fun AboutTheApp(
    appVersion: String,
    onHelpClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        CardListItem(
            modifier = Modifier.semantics(mergeDescendants = true) { },
            isFirst = true,
            isLast = false
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(GovUkTheme.spacing.medium),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BodyRegularLabel(
                    text = stringResource(R.string.version_setting),
                    modifier = Modifier.weight(1f)
                )

                BodyRegularLabel(text = appVersion)
            }
        }

        ExternalLinkListItem(
            title = stringResource(R.string.help_and_feedback_title),
            onClick = onHelpClick,
            isFirst = false,
            isLast = true
        )
    }
}

@Composable
private fun PrivacyPolicy(
    onPrivacyPolicyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ExternalLinkListItem(
        title = stringResource(R.string.privacy_policy_title),
        onClick = onPrivacyPolicyClick,
        modifier = modifier,
        isFirst = true,
        isLast = false,
    )
}

@Composable
private fun AccessibilityStatement(
    onAccessibilityStatementClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ExternalLinkListItem(
        title = stringResource(R.string.accessibility_title),
        onClick = onAccessibilityStatementClick,
        modifier = modifier,
        isFirst = false,
        isLast = false,
    )
}

@Composable
private fun OpenSourceLicenses(
    onLicenseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    InternalLinkListItem(
        title = stringResource(R.string.oss_licenses_title),
        onClick = onLicenseClick,
        modifier = modifier,
        isFirst = false,
        isLast = false,
    )
}

@Composable
private fun TermsAndConditions(
    onLicenseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ExternalLinkListItem(
        title = stringResource(R.string.terms_and_conditions_title),
        onClick = onLicenseClick,
        modifier = modifier,
        isFirst = false,
        isLast = true,
    )
}

@Composable
internal fun SettingsScreenWrapper(
    state: SettingsUiState,
    notificationStatusResId: Int = R.string.off_button
) {
    GovUkTheme {
        SettingsScreen(
            uiState = state,
            appVersion = "1.0.0",
            notificationStatusResId = notificationStatusResId,
            actions = SettingsActions()
        )
    }
}

@Preview
@Composable
private fun SettingsScreenPreview() {
    SettingsScreenWrapper(
        state = SettingsUiState(
            userEmail = "user@gov.uk",
            isNotificationsEnabled = true,
            isAuthenticationEnabled = true,
            isAnalyticsEnabled = true
        ),
        notificationStatusResId = R.string.on_button
    )
}
