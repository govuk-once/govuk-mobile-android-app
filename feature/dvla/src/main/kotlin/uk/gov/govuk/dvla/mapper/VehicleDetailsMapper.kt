package uk.gov.govuk.dvla.mapper

import uk.gov.govuk.config.data.remote.model.DvlaUrls
import uk.gov.govuk.dvla.domain.VehicleDetails
import uk.gov.govuk.dvla.ui.model.KeeperUiModel
import uk.gov.govuk.dvla.ui.model.VehicleDetailsUiModel
import javax.inject.Inject

internal class VehicleDetailsMapper @Inject constructor(
    private val taxAndMotStatusMapper: TaxAndMotStatusMapper,
    private val specificationsMapper: SpecificationsMapper
) {
    fun toUiModel(vehicle: VehicleDetails, dvlaUrls: DvlaUrls?): VehicleDetailsUiModel {
        return VehicleDetailsUiModel(
            make = vehicle.summary.make,
            model = vehicle.summary.model ?: "",
            registration = vehicle.summary.registration,
            keeper = vehicle.getKeeper(),
            specificationsIcons = listOf(
                specificationsMapper.getCalendarSpecification(vehicle.dateOfFirstRegistration),
                specificationsMapper.getFuelTypeSpecification(vehicle.fuelType),
                specificationsMapper.getColourSpecification(vehicle.colour)
            ),
            taxStatus = taxAndMotStatusMapper.getTaxStatus(vehicle.summary, dvlaUrls),
            motStatus = taxAndMotStatusMapper.getMotStatus(vehicle.summary, dvlaUrls),
            specifications = with(specificationsMapper) {
                listOf(
                    createMakeItem(vehicle),
                    createModelItem(vehicle),
                    createFirstRegisteredItem(vehicle),
                    createFuelTypeItem(vehicle),
                    createColourItem(vehicle),
                    createEngineSizeItem(vehicle),
                    createEmissionsItem(vehicle)
                )
            }
        )
    }

    private fun VehicleDetails.getKeeper(): KeeperUiModel {
        val name = listOfNotNull(
            keeperTitle,
            keeperFirstNames,
            keeperLastName
        ).joinToString(separator = " ")

        val addressLines = keeperFullAddress
            ?.split("\n")
            ?.filter { it.isNotBlank() }
            ?: emptyList()

        return KeeperUiModel(name = name, addressLines = addressLines)
    }
}
