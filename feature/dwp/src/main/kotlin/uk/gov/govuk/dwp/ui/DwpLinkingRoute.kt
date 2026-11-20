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
import uk.gov.govuk.dwp.DwpLinkViewModel
import uk.gov.govuk.dwp.R

@Composable
internal fun DwpLinkingRoute(
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: DwpLinkViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    DwpLinkScreen(
        state = state,
        onClose = onClose,
        onContinue = if (state is DwpLinkViewModel.UiState.Error) viewModel::onRetryClicked else viewModel::onIntroContinueClicked,
        modifier = modifier
    )
}

@Composable
internal fun DwpLinkScreen(
    state: DwpLinkViewModel.UiState,
    onClose: () -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    // The linking operation is canceled with the navigation entry's ViewModel when leaving.
    BackHandler(onBack = onClose)
    Box(
        modifier = modifier.fillMaxSize()
            .background(GovUkTheme.colourScheme.surfaces.fullScreenLinkAccount)
            .safeDrawingPadding()
    ) {
        when (state) {
            DwpLinkViewModel.UiState.Default, is DwpLinkViewModel.UiState.Error -> DwpLinkIntroScreen(
                onClose = onClose,
                onContinue = onContinue,
                showError = state is DwpLinkViewModel.UiState.Error
            )

            DwpLinkViewModel.UiState.Loading.Auth,
            DwpLinkViewModel.UiState.Loading.Linking -> BookendConnectingScreen(title = stringResource(R.string.dwp_linking))

            DwpLinkViewModel.UiState.Success -> AccountConnectionSuccessScreen(
                title = stringResource(R.string.dwp_success_title),
                buttonText = stringResource(R.string.dwp_continue),
                onContinue = onClose
            )
        }
    }
}
