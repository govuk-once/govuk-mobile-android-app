package uk.gov.govuk.dvla.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import uk.gov.govuk.dvla.DvlaLinkWidgetViewModel

@Composable
fun DvlaLinkHeader(
    linkResult: Boolean,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: DvlaLinkWidgetViewModel = hiltViewModel()
    val state by viewModel.dvlaState.collectAsState()

    LaunchedEffect(linkResult) {
        viewModel.checkStatus()
    }

    DvlaLinkCard(
        state = state,
        onActionClick = { cardText ->
            viewModel.onLinkCardClicked(cardText)
            onActionClick()
        },
        modifier = modifier
    )
}
