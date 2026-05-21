package uk.gov.govuk.dvla.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
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
import uk.gov.govuk.design.ui.component.FixedDoubleButtonGroup
import uk.gov.govuk.design.ui.component.FullScreenWrapper
import uk.gov.govuk.design.ui.component.RunOnceLaunchedEffect
import uk.gov.govuk.design.ui.component.error.DeviceOfflineScreen
import uk.gov.govuk.design.ui.component.error.ErrorConstants
import uk.gov.govuk.design.ui.component.error.ErrorPage
import uk.gov.govuk.design.ui.model.Button
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.dvla.DvlaViewModel
import uk.gov.govuk.dvla.R

@Composable
internal fun DvlaLinkingRoute(
    onLaunchBrowser: (String) -> Unit,
    onLinkComplete: () -> Unit,
    onUnlinkComplete: () -> Unit,
    onWebFlowClosed: () -> Unit,
    modifier: Modifier = Modifier
) {

    val viewModel: DvlaViewModel = hiltViewModel()
    val authUrlToLaunch by viewModel.authUrlToLaunch.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    var browserLaunched by rememberSaveable { mutableStateOf(false) }

    BackHandler {
        onWebFlowClosed()
    }

    // handle Chrome tab cancellation
    LifecycleResumeEffect(Unit) {
        if (browserLaunched) {
            onWebFlowClosed()
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
            when (it) {
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
                    viewModel.onSuccessContinueClicked(buttonText)
                },
                modifier = modifier
            )
        }

        is DvlaViewModel.UiState.Loading -> {
            DvlaLinkLoadingScreen(
                modifier = modifier
            )
        }

        is DvlaViewModel.UiState.Error.Offline -> {
            DvlaOfflineScreen(
                onTryAgain = { viewModel.onRetryClicked() },
                modifier = modifier
            )

        }

        is DvlaViewModel.UiState.Error.Other -> {
            DvlaLinkErrorScreen(
                onPageView = { title ->
                    viewModel.onErrorOtherPageView(title)
                },
                onBackToDrivingClicked = { buttonText ->
                    viewModel.onErrorBackToDrivingClicked(buttonText)
                    onWebFlowClosed()
                },
                onVisitGovUkClicked = { buttonText, url ->
                    viewModel.onErrorVisitGovUkClicked(text = buttonText, url = url)
                    onLaunchBrowser(url)
                },
                modifier = modifier
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

    RunOnceLaunchedEffect {
        onPageView(title)
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

@Composable
private fun DvlaOfflineScreen(
    onTryAgain: () -> Unit,
    modifier: Modifier = Modifier
) {
    FullScreenWrapper(modifier = modifier) {
        DeviceOfflineScreen(
            onTryAgain = onTryAgain
        )
    }
}

@Composable
private fun DvlaLinkErrorScreen(
    onPageView: (String) -> Unit,
    onBackToDrivingClicked: (String) -> Unit,
    onVisitGovUkClicked: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val title = stringResource(R.string.link_dvla_problem_title)
    val primaryText = stringResource(R.string.link_dvla_problem_primary_button)
    val secondaryText = stringResource(R.string.link_dvla_problem_secondary_button)

    RunOnceLaunchedEffect {
        onPageView(title)
    }

    ErrorPage(
        headerText = title,
        subText = listOf(stringResource(R.string.link_dvla_problem_description)),
        footerContent = {
            FixedDoubleButtonGroup(
                primaryButton = Button(
                    text = primaryText,
                    onClick = { onBackToDrivingClicked(primaryText) }
                ),
                secondaryButton = Button(
                    text = secondaryText,
                    onClick = {
                        onVisitGovUkClicked(secondaryText, ErrorConstants.GOV_UK_URL)
                    },
                    isExternal = true
                )
            )
        },
        modifier = modifier.safeDrawingPadding()
    )
}
