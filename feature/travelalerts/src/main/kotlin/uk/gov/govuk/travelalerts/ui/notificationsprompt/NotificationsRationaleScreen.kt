package uk.gov.govuk.travelalerts.ui.notificationsprompt

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import uk.gov.govuk.design.ui.component.InfoAlert
import uk.gov.govuk.design.ui.component.OnboardingSlide
import uk.gov.govuk.design.ui.component.PrivacyPolicyLink
import uk.gov.govuk.design.ui.model.Button
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.notifications.ui.NotificationsSettingsAlert
import uk.gov.govuk.notifications.ui.getNotificationsPermissionStatus
import uk.gov.govuk.notifications.ui.openDeviceNotificationsSettings
import uk.gov.govuk.travelalerts.R
import uk.gov.govuk.travelalerts.navigation.COUNTRY_LIST_ROUTE

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun NotificationsRationaleScreen(
    countrySlug: String,
    onBack: () -> Unit,
    navController: NavController,
    launchBrowser: (url: String) -> Unit,
    viewModel: NotificationsRationaleViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsState()
    val permissionStatus = getNotificationsPermissionStatus()
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(Unit) {
        viewModel.onPageView(countrySlug)
    }

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect {
            navController.popBackStack(COUNTRY_LIST_ROUTE, inclusive = true)
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

    when (val state = uiState.value) {
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
                onNotNow = { viewModel.onNotNow(countrySlug) },
                onAgreeContinue = { viewModel.onAgreeToContinue(permissionStatus) },
                launchBrowser = launchBrowser
            )
        }

        is NotificationsRationaleViewModel.State.Alert -> {
            val context = LocalContext.current
            NotificationsRationaleScreenContent(
                onNotNow = { viewModel.onNotNow(countrySlug) },
                onAgreeContinue = { viewModel.onAgreeToContinue(permissionStatus) },
                launchBrowser = launchBrowser,
                showSettingsAlert = true,
                onSettingsAlertCancel = { viewModel.onSettingsAlertCancel(countrySlug) },
                onSettingsAlertContinue = { openDeviceNotificationsSettings(context) }
            )
        }

        is NotificationsRationaleViewModel.State.Error -> {
            InfoAlert(
                title = R.string.follow_country_error_title,
                message = R.string.follow_country_error_description,
                buttonText = R.string.follow_country_error_button,
                onDismiss = {
                    viewModel.onDismissError()
                    onBack()
                }
            )
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun NotificationsRationaleScreenContent(
    onNotNow: () -> Unit,
    onAgreeContinue: () -> Unit,
    launchBrowser: (url: String) -> Unit,
    showSettingsAlert: Boolean = false,
    onSettingsAlertCancel: () -> Unit = {},
    onSettingsAlertContinue: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        OnboardingSlide(
            title = R.string.notifications_rationale_title,
            body = R.string.notifications_rationale_body,
            image = uk.gov.govuk.notifications.R.drawable.notifications_bell,
            privacyPolicy = {
                PrivacyPolicyLink(
                    onClick = { _, url -> launchBrowser(url) }
                )
            }
        )

        Box(modifier = Modifier.weight(1f))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = GovUkTheme.spacing.medium)
        ) {
            uk.gov.govuk.design.ui.component.FixedDoubleButtonGroup(
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
            onDismiss = { onSettingsAlertCancel() }
        )
    }
}
