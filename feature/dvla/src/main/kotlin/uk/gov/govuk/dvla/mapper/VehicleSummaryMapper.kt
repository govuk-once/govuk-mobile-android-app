package uk.gov.govuk.dvla.mapper

import uk.gov.govuk.config.data.remote.model.DvlaUrls
import uk.gov.govuk.dvla.domain.TaxStatus
import uk.gov.govuk.dvla.domain.VehicleSummary
import uk.gov.govuk.dvla.ui.model.VehicleSummaryUiModel
import javax.inject.Inject

internal class VehicleSummaryMapper @Inject constructor(
    private val taxAndMotStatusMapper: TaxAndMotStatusMapper,
    private val vehicleMenuItemsMapper: VehicleMenuItemsMapper
) {
    fun toUiModel(vehicle: VehicleSummary, dvlaUrls: DvlaUrls?): VehicleSummaryUiModel {
        return VehicleSummaryUiModel(
            vehicleId = vehicle.vehicleId,
            registration = vehicle.registration,
            make = vehicle.make,
            model = vehicle.model ?: "",
            taxStatus = taxAndMotStatusMapper.getTaxStatus(vehicle = vehicle, dvlaUrls = dvlaUrls),
            motStatus = taxAndMotStatusMapper.getMotStatus(vehicle = vehicle, dvlaUrls = dvlaUrls),
            menuItems = vehicleMenuItemsMapper.buildMenuItems(
                hasSorn = vehicle.sornStart != null,
                isTaxed = vehicle.taxStatus == TaxStatus.TAXED,
                dvlaUrls = dvlaUrls
            )
        )
    }
}