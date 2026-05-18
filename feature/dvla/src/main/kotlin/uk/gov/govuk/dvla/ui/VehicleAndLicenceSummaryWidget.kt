package uk.gov.govuk.dvla.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import uk.gov.govuk.design.ui.component.LoaderCard
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.dvla.VehicleAndLicenceSummaryUiState
import uk.gov.govuk.dvla.VehicleAndLicenceSummaryViewModel
import uk.gov.govuk.dvla.ui.component.VehicleSummaryCard

@Composable
fun VehicleAndLicenceSummaryWidget(
    modifier: Modifier = Modifier,
) {
    val viewModel: VehicleAndLicenceSummaryViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()

    when (val currentState = state) {
        is VehicleAndLicenceSummaryUiState.Hidden -> return // draw nothing if not linked
        is VehicleAndLicenceSummaryUiState.Loading -> {
            LoaderCard(
                modifier = modifier.fillMaxWidth()
                // TODO alt text?
            )
        }
        is VehicleAndLicenceSummaryUiState.Error -> {
            // TODO placeholder for now, tbc in future tickets
        }
        is VehicleAndLicenceSummaryUiState.Success -> {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                currentState.vehicles.forEach { vehicle ->
                    VehicleSummaryCard(
                        vehicleSummary = vehicle,
                        onDetailsClick = {
                            // TODO to be handled in next ticket(s)
                        },
                        onMoreClick = {
                            // TODO to be handled in next ticket(s)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}