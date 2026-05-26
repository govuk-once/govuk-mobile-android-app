package uk.gov.govuk.dvla.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import uk.gov.govuk.design.ui.component.SmallVerticalSpacer
import uk.gov.govuk.dvla.domain.DvlaLinkState
import uk.gov.govuk.dvla.DvlaLinkWidgetViewModel
import uk.gov.govuk.dvla.ui.component.DvlaLinkCard

@Composable
fun DvlaLinkHeader(
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: DvlaLinkWidgetViewModel = hiltViewModel()
    val state by viewModel.dvlaState.collectAsState()

    when (state) {
        DvlaLinkState.UNLINKED, DvlaLinkState.CHECKING -> {
            Column(modifier = modifier) {
                DvlaLinkCard(
                    state = state,
                    onActionClick = { cardText ->
                        viewModel.onLinkCardClicked(cardText)
                        onActionClick()
                    }
                )
                SmallVerticalSpacer()
            }
        }
        DvlaLinkState.LINKED -> { /* Show nothing */ }
    }
}
