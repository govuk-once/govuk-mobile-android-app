package uk.gov.govuk.dwp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import uk.gov.govuk.data.identity.model.ServiceLinkStatus
import uk.gov.govuk.design.ui.component.NonTappableCard
import uk.gov.govuk.design.ui.component.SmallVerticalSpacer
import uk.gov.govuk.dwp.AuthLinkWidgetViewModel
import uk.gov.govuk.dwp.R
import uk.gov.govuk.dwp.ui.component.DwpLinkCard

@Composable
fun DwpLinkHeader(
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: AuthLinkWidgetViewModel = hiltViewModel()
    val state by viewModel.dwpState.collectAsState(initial = ServiceLinkStatus.CHECKING)
    DwpLinkHeaderContent(
        state = state,
        onActionClick = {
            onActionClick()
        },
        modifier = modifier
    )
}

@Composable
internal fun DwpLinkHeaderContent(
    state: ServiceLinkStatus,
    onActionClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        when (state) {
            ServiceLinkStatus.LINKED -> NonTappableCard(body = stringResource(R.string.dwp_success_title))
            else -> DwpLinkCard(state = state, onActionClick = onActionClick)
        }
        SmallVerticalSpacer()
    }
}
