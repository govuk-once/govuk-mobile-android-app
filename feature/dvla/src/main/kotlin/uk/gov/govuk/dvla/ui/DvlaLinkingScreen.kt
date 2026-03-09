package uk.gov.govuk.dvla.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import uk.gov.govuk.design.ui.component.InfoAlert
import uk.gov.govuk.design.ui.component.LoadingScreen
import uk.gov.govuk.dvla.DvlaViewModel
import uk.gov.govuk.dvla.R

@Composable
internal fun DvlaLinkingRoute(
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {

    val viewModel: DvlaViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    if (uiState is DvlaViewModel.UiState.Error) {
        InfoAlert(
            title = R.string.error_dialog_title,
            message = R.string.error_dialog_message,
            buttonText = R.string.try_again,
            onDismiss = {
                onComplete()
            }
        )
    }

    LaunchedEffect(Unit) {
        viewModel.linkingEvent.collect {
            when(it) {
                is DvlaViewModel.LinkingEvent.LinkComplete -> onComplete()
            }
        }
    }

    DvlaLinkingScreen(
        modifier = modifier
    )
}

@Composable
private fun DvlaLinkingScreen(
    modifier: Modifier = Modifier
) {
    LoadingScreen(
        accessibilityText = "Linking your account",
        modifier = modifier
    )
}