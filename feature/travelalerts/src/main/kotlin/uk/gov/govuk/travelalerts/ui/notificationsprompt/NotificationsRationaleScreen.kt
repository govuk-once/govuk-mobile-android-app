package uk.gov.govuk.travelalerts.ui.notificationsprompt

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import androidx.window.core.layout.WindowSizeClass
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.FixedDoubleButtonGroup
import uk.gov.govuk.design.ui.component.LargeTitleBoldLabel
import uk.gov.govuk.design.ui.component.LargeVerticalSpacer
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.component.PrivacyPolicyLink
import uk.gov.govuk.design.ui.component.SmallVerticalSpacer
import uk.gov.govuk.design.ui.model.Button
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.notifications.R.drawable.notifications_bell
import uk.gov.govuk.notifications.ui.NotificationsSettingsAlert
import uk.gov.govuk.notifications.ui.getNotificationsPermissionStatus
import uk.gov.govuk.notifications.ui.openDeviceNotificationsSettings
import uk.gov.govuk.travelalerts.R
import uk.gov.govuk.travelalerts.navigation.COUNTRY_LIST_ROUTE
import uk.gov.govuk.travelalerts.navigation.EDIT_COUNTRIES_ROUTE
import uk.gov.govuk.travelalerts.navigation.EDIT_NOTIFICATIONS_ENABLED_KEY
import uk.gov.govuk.travelalerts.navigation.EDIT_OPT_IN_ERROR_KEY
import uk.gov.govuk.travelalerts.navigation.EDIT_REOPEN_SLUG_KEY
import uk.gov.govuk.travelalerts.navigation.SHOW_ERROR_ARG
import uk.gov.govuk.travelalerts.navigation.TRAVEL_ALERTS_FOLLOW_ERROR_KEY

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun NotificationsRationaleScreen(
    countrySlug: String,
    navController: NavController,
    launchBrowser: (url: String) -> Unit,
    origin: NotificationsRationaleViewModel.Origin = NotificationsRationaleViewModel.Origin.FOLLOW,
    viewModel: NotificationsRationaleViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsState()
    val permissionStatus = getNotificationsPermissionStatus()
    val lifecycleOwner = LocalLifecycleOwner.current
    val agreeText = stringResource(R.string.notifications_rationale_agree)
    val notNowText = stringResource(R.string.notifications_rationale_not_now)
    val privacyText = stringResource(R.string.notifications_rationale_privacy_link)

    LaunchedEffect(Unit) {
        viewModel.onPageView(countrySlug, origin)
    }

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is NotificationsRationaleViewModel.NavigationEvent.ExitToTopic -> {
                    if (event.error) {
                        val hasEditCountries = try {
                            navController.getBackStackEntry(EDIT_COUNTRIES_ROUTE)
                            true
                        } catch (_: IllegalArgumentException) {
                            false
                        }
                        if (hasEditCountries) {
                            navController.navigate("$EDIT_COUNTRIES_ROUTE?$SHOW_ERROR_ARG=true") {
                                popUpTo(EDIT_COUNTRIES_ROUTE) { inclusive = true }
                            }
                        } else {
                            navController.popBackStack(COUNTRY_LIST_ROUTE, inclusive = true)
                            navController.currentBackStackEntry
                                ?.savedStateHandle
                                ?.set(TRAVEL_ALERTS_FOLLOW_ERROR_KEY, true)
                        }
                    } else {
                        navController.popBackStack(COUNTRY_LIST_ROUTE, inclusive = true)
                    }
                }
                is NotificationsRationaleViewModel.NavigationEvent.ReturnToEdit -> {
                    navController.previousBackStackEntry?.savedStateHandle?.apply {
                        set(EDIT_REOPEN_SLUG_KEY, event.slug)
                        set(EDIT_OPT_IN_ERROR_KEY, event.error)
                        set(EDIT_NOTIFICATIONS_ENABLED_KEY, event.notificationsEnabled)
                    }
                    navController.popBackStack()
                }
            }
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.onResume(countrySlug)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    BackHandler(enabled = origin == NotificationsRationaleViewModel.Origin.EDIT) {
        viewModel.onNotNow(countrySlug, notNowText)
    }

    when (uiState.value) {
        is NotificationsRationaleViewModel.State.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = GovUkTheme.colourScheme.surfaces.primary
                )
            }
        }

        is NotificationsRationaleViewModel.State.Default -> {
            NotificationsRationaleScreenContent(
                onNotNow = { viewModel.onNotNow(countrySlug, notNowText) },
                onAgreeContinue = { viewModel.onAgreeToContinue(permissionStatus, agreeText) },
                onPrivacyLinkClick = { viewModel.onPrivacyLinkClick(privacyText) },
                launchBrowser = launchBrowser
            )
        }

        is NotificationsRationaleViewModel.State.Alert -> {
            val context = LocalContext.current
            NotificationsRationaleScreenContent(
                onNotNow = { viewModel.onNotNow(countrySlug, notNowText) },
                onAgreeContinue = { viewModel.onAgreeToContinue(permissionStatus, agreeText) },
                onPrivacyLinkClick = { viewModel.onPrivacyLinkClick(privacyText) },
                launchBrowser = launchBrowser,
                showSettingsAlert = true,
                onSettingsAlertCancel = { viewModel.onSettingsAlertCancelClicked() },
                onSettingsAlertContinue = { viewModel.onSettingsAlertContinue() },
                onSettingsAlertDismiss = { viewModel.onSettingsAlertDismissed() },
                openSettings = { openDeviceNotificationsSettings(context) }
            )
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun NotificationsRationaleScreenContent(
    modifier: Modifier = Modifier,
    onNotNow: () -> Unit,
    onAgreeContinue: () -> Unit,
    onPrivacyLinkClick: () -> Unit,
    launchBrowser: (url: String) -> Unit,
    showSettingsAlert: Boolean = false,
    onSettingsAlertCancel: () -> Unit = {},
    onSettingsAlertContinue: () -> Unit = {},
    onSettingsAlertDismiss: () -> Unit = {},
    openSettings: () -> Unit = {}
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        val windowAdaptiveInfo = currentWindowAdaptiveInfo()
        val isWindowHeightNotCompact =
            windowAdaptiveInfo.windowSizeClass.isHeightAtLeastBreakpoint(WindowSizeClass.HEIGHT_DP_MEDIUM_LOWER_BOUND)
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
                .padding(top = GovUkTheme.spacing.extraLarge)
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (isWindowHeightNotCompact) {
                Image(
                    painter = painterResource(notifications_bell),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(
                            horizontal = GovUkTheme.spacing.extraLarge,
                            vertical = GovUkTheme.spacing.medium
                        )
                )
            }

            LargeTitleBoldLabel(
                text = stringResource(R.string.notifications_rationale_title),
                modifier = Modifier
                    .focusRequester(remember { FocusRequester() })
                    .focusable()
                    .padding(horizontal = GovUkTheme.spacing.extraLarge)
                    .semantics { heading() },
                textAlign = TextAlign.Center
            )
            MediumVerticalSpacer()
            BodyRegularLabel(
                text = stringResource(R.string.notifications_rationale_body_part1),
                modifier = Modifier
                    .focusable()
                    .padding(horizontal = GovUkTheme.spacing.extraLarge),
                textAlign = TextAlign.Center
            )
            SmallVerticalSpacer()
            BodyRegularLabel(
                text = stringResource(R.string.notifications_rationale_body_part2),
                modifier = Modifier
                    .focusable()
                    .padding(horizontal = GovUkTheme.spacing.extraLarge),
                textAlign = TextAlign.Center
            )
            SmallVerticalSpacer()

            PrivacyPolicyLink(
                text = stringResource(R.string.notifications_rationale_privacy_link),
                onClick = { _, url ->
                    onPrivacyLinkClick()
                    launchBrowser(url)
                }
            )
            LargeVerticalSpacer()
            MediumVerticalSpacer()
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = GovUkTheme.spacing.medium)
        ) {
            FixedDoubleButtonGroup(
                primaryButton = Button(
                    text = stringResource(R.string.notifications_rationale_agree),
                    onClick = onAgreeContinue
                ),
                secondaryButton = Button(
                    text = stringResource(R.string.notifications_rationale_not_now),
                    onClick = onNotNow
                )
            )
        }
    }

    if (showSettingsAlert) {
        NotificationsSettingsAlert(
            onContinueButtonClick = { onSettingsAlertContinue() },
            onCancelButtonClick = { onSettingsAlertCancel() },
            onDismiss = { onSettingsAlertDismiss() },
            openSettings = openSettings
        )
    }
}

@PreviewLightDark
@Composable
private fun NotificationsRationaleScreenContentPreview() {
    GovUkTheme {
        NotificationsRationaleScreenContent(
            onNotNow = {},
            onAgreeContinue = {},
            onPrivacyLinkClick = {},
            launchBrowser = {}
        )
    }
}
