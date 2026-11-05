package uk.gov.govuk.dvla.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import uk.gov.govuk.data.identity.model.ServiceLinkStatus
import uk.gov.govuk.design.ui.component.SmallVerticalSpacer
import uk.gov.govuk.dvla.DvlaLinkWidgetViewModel
import uk.gov.govuk.dvla.ui.component.DvlaLinkCard

@Composable
fun DvlaLinkHeader(
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: DvlaLinkWidgetViewModel = hiltViewModel()
    val state by viewModel.dvlaState.collectAsState(initial = ServiceLinkStatus.CHECKING)

    when (state) {
        ServiceLinkStatus.UNLINKED,
        ServiceLinkStatus.CHECKING -> {
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
        ServiceLinkStatus.LINKED -> { /* Show nothing */ }
        ServiceLinkStatus.ERROR -> { /* Show nothing */ }
    }
}
