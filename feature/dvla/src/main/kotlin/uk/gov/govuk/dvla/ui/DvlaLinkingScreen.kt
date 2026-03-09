package uk.gov.govuk.dvla.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import uk.gov.govuk.design.ui.component.LoadingScreen
import uk.gov.govuk.dvla.DvlaViewModel

@Composable
internal fun DvlaLinkingRoute(
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {

    val viewModel: DvlaViewModel = hiltViewModel()

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