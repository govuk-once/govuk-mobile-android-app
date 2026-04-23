package uk.gov.govuk.dvla.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import uk.gov.govuk.design.ui.component.AccountConnectionSuccessScreen
import uk.gov.govuk.design.ui.component.BookendConnectingScreen
import uk.gov.govuk.design.ui.component.InfoAlert
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.dvla.DvlaViewModel
import uk.gov.govuk.dvla.R

@Composable
internal fun DvlaLinkingRoute(
    onLaunchBrowser: (String) -> Unit,
    onLinkComplete: () -> Unit,
    onUnlinkComplete: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {

    val viewModel: DvlaViewModel = hiltViewModel()
    val authUrlToLaunch by viewModel.authUrlToLaunch.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    var browserLaunched by rememberSaveable { mutableStateOf(false) }

    BackHandler {
        onClose()
    }

    // exit screen if user closes Chrome tab
    LifecycleResumeEffect(Unit) {
        if (browserLaunched) {
            onClose()
        }
        onPauseOrDispose {
            // nothing to clean up
        }
    }

    LaunchedEffect(authUrlToLaunch) {
        authUrlToLaunch?.let { url ->
            onLaunchBrowser(url)
            viewModel.onAuthTabLaunched()
            browserLaunched = true
        }
    }

    LaunchedEffect(Unit) {
        viewModel.linkingEvent.collect {
            when(it) {
                is DvlaViewModel.LinkingEvent.LinkComplete -> onLinkComplete()
                is DvlaViewModel.LinkingEvent.UnlinkComplete -> onUnlinkComplete()
            }
        }
    }

    when (uiState) {
        is DvlaViewModel.UiState.Default -> Unit // don't need to show anything
        is DvlaViewModel.UiState.Success -> {
            DvlaLinkSuccessScreen(
                onPageView = { screenTitle ->
                    viewModel.onLinkSuccessPageView(screenTitle)
                },
                onContinue = { buttonText ->
                    viewModel.onSuccessContinueClicked(buttonText) },
                modifier = modifier
            )
        }
        is DvlaViewModel.UiState.Loading -> {
            DvlaLinkLoadingScreen(
                modifier = modifier
            )
        }
        is DvlaViewModel.UiState.Error -> {
            InfoAlert(
                title = R.string.error_dialog_title,
                message = R.string.error_dialog_message,
                buttonText = R.string.try_again,
                onDismiss = {
                    onClose()
                }
            )
        }
    }
}

@Composable
private fun DvlaLinkLoadingScreen(
    modifier: Modifier = Modifier
) {
    BookendConnectingScreen(
        title = stringResource(R.string.link_dvla_connecting_title),
        modifier = modifier
    )
}

@Composable
private fun DvlaLinkSuccessScreen(
    onPageView: (String) -> Unit,
    onContinue: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val title = stringResource(R.string.link_dvla_success_title)
    val buttonText = stringResource(R.string.link_dvla_success_button)

    var hasTrackedPageView by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        // protect against config change
        if (!hasTrackedPageView) {
            onPageView(title)
            hasTrackedPageView = true
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(GovUkTheme.colourScheme.surfaces.fullScreenLinkAccount)
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        AccountConnectionSuccessScreen(
            title = title,
            buttonText = buttonText,
            onContinue = { onContinue(buttonText) },
            modifier = Modifier
        )
    }
}
