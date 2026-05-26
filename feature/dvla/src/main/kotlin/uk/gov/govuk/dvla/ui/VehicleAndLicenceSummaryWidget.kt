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
import uk.gov.govuk.design.ui.component.SmallVerticalSpacer
import uk.gov.govuk.dvla.VehicleAndLicenceSummaryUiState
import uk.gov.govuk.dvla.VehicleAndLicenceSummaryViewModel
import uk.gov.govuk.dvla.ui.component.VehicleSummaryCard
import uk.gov.govuk.dvla.ui.model.VehicleSummaryUiModel

@Composable
fun VehicleAndLicenceSummaryWidget(
    modifier: Modifier = Modifier,
) {
    val viewModel: VehicleAndLicenceSummaryViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()

    when (val currentState = state) {
        is VehicleAndLicenceSummaryUiState.Hidden -> return // draw nothing if not linked
        is VehicleAndLicenceSummaryUiState.Loading -> VehicleAndLicenceSummaryLoading(modifier = modifier)
        is VehicleAndLicenceSummaryUiState.Error -> {
            // TODO placeholder for now, tbc in future tickets
        }
        is VehicleAndLicenceSummaryUiState.Success -> {
            VehicleAndLicenceSummarySuccess(
                vehicles = currentState.vehicles,
                onDetailsClick = {
                    // TODO to be handled in next ticket(s)
                },
                onMoreClick = {
                    // TODO to be handled in next ticket(s)
                },
                modifier = modifier
            )
        }
    }
}

@Composable
private fun VehicleAndLicenceSummaryLoading(
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        LoaderCard(modifier = Modifier.fillMaxWidth())
        SmallVerticalSpacer()
    }
}

@Composable
private fun VehicleAndLicenceSummarySuccess(
    vehicles: List<VehicleSummaryUiModel>,
    onDetailsClick: () -> Unit,
    onMoreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        vehicles.forEach { vehicle ->
            VehicleSummaryCard(
                vehicleSummary = vehicle,
                onDetailsClick = { onDetailsClick() },
                onMoreClick = { onMoreClick() },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}