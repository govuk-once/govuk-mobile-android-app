package uk.gov.govuk.dwp.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import uk.gov.govuk.design.ui.component.AccountConnectionSuccessScreen
import uk.gov.govuk.design.ui.component.BookendConnectingScreen
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.dwp.AuthLinkViewModel
import uk.gov.govuk.dwp.R

@Composable
internal fun DwpLinkingRoute(
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: AuthLinkViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    DwpLinkScreen(
        state = state,
        onClose = onClose,
        onContinue = if (state is AuthLinkViewModel.UiState.Error) viewModel::onRetryClicked else viewModel::onIntroContinueClicked,
        modifier = modifier
    )
}

@Composable
internal fun DwpLinkScreen(
    state: AuthLinkViewModel.UiState,
    onClose: () -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    // The linking operation is cancelled with the navigation entry's ViewModel when leaving.
    BackHandler(onBack = onClose)
    Box(
        modifier = modifier.fillMaxSize()
            .background(GovUkTheme.colourScheme.surfaces.fullScreenLinkAccount)
            .safeDrawingPadding()
    ) {
        when (state) {
            AuthLinkViewModel.UiState.Default, is AuthLinkViewModel.UiState.Error -> DwpLinkIntroScreen(
                onClose = onClose,
                onContinue = onContinue,
                showError = state is AuthLinkViewModel.UiState.Error
            )
            AuthLinkViewModel.UiState.Loading.Auth,
            AuthLinkViewModel.UiState.Loading.Linking -> BookendConnectingScreen(title = stringResource(R.string.dwp_linking))
            AuthLinkViewModel.UiState.Success -> AccountConnectionSuccessScreen(
                title = stringResource(R.string.dwp_success_title),
                buttonText = stringResource(R.string.dwp_continue),
                onContinue = onClose
            )
        }
    }
}
