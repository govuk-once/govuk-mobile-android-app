package uk.gov.govuk.dvla.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import uk.gov.govuk.design.ui.component.LoaderCard
import uk.gov.govuk.design.ui.component.SmallVerticalSpacer
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.dvla.VehicleAndLicenceSummaryUiState
import uk.gov.govuk.dvla.VehicleAndLicenceSummaryViewModel
import uk.gov.govuk.dvla.VehicleUiState
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
        is VehicleAndLicenceSummaryUiState.Content -> {

            when (val vehicleState = currentState.vehicleState) {
                is VehicleUiState.Loading -> VehicleAndLicenceSummaryLoading(modifier = modifier)
                is VehicleUiState.Error -> {
                    // TODO placeholder for now, tbc in future tickets
                }
                is VehicleUiState.Success -> {
                    VehicleSummarySuccess(
                        vehicles = vehicleState.vehicles,
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

//            when (val licenceState = currentState.licenceState) {
//                is LicenceUiState.Loading -> VehicleAndLicenceSummaryLoading(modifier = modifier)
//                is LicenceUiState.Error -> {  }
//                is LicenceUiState.Success -> {
//                    // LicenceSummaryCard(licence = licenceState.licence)
//                }
//            }

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
private fun VehicleSummarySuccess(
    vehicles: List<VehicleSummaryUiModel>,
    onDetailsClick: () -> Unit,
    onMoreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(GovUkTheme.spacing.medium)
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