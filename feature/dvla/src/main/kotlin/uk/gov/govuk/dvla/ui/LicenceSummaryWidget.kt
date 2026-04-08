package uk.gov.govuk.dvla.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import uk.gov.govuk.design.ui.theme.GovUkTheme
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
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(GovUkTheme.spacing.medium),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = GovUkTheme.colourScheme.surfaces.primary
                )
            }
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