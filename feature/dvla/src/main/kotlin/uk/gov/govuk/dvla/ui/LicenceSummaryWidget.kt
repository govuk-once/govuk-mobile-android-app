package uk.gov.govuk.dvla.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import uk.gov.govuk.dvla.LicenceSummaryState
import uk.gov.govuk.dvla.LicenceSummaryViewModel

@Composable
fun LicenceSummaryWidget(
    modifier: Modifier = Modifier,
) {
    val viewModel: LicenceSummaryViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()

    when (val currentState = state) {
        is LicenceSummaryState.Hidden -> return // draw nothing if not linked
        is LicenceSummaryState.Loading -> {
            // TODO placeholder for now, tbc in future tickets
        }
        is LicenceSummaryState.Error -> {
            // TODO placeholder for now, tbc in future tickets
        }
        is LicenceSummaryState.Success -> {
            LicenceDetailsCard(
                details = currentState.licence,
                modifier = modifier
            )
        }
    }
}