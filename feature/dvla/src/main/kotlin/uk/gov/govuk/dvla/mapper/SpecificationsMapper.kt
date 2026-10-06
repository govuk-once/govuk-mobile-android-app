package uk.gov.govuk.dvla.mapper

import uk.gov.govuk.design.ui.model.AccessibleString
import uk.gov.govuk.design.ui.model.InternalLinkListItemModel
import uk.gov.govuk.design.ui.model.SpecificationIconUiModel
import uk.gov.govuk.dvla.R
import uk.gov.govuk.dvla.domain.FuelType
import uk.gov.govuk.dvla.domain.VehicleColour
import uk.gov.govuk.dvla.domain.VehicleDetails
import uk.gov.govuk.dvla.util.StringProvider
import uk.gov.govuk.dvla.util.getFormattedEngineCapacity
import uk.gov.govuk.dvla.util.toMonthYearDisplayFormat
import uk.gov.govuk.dvla.util.toYearDisplayFormat
import java.time.LocalDate
import javax.inject.Inject

internal class SpecificationsMapper @Inject constructor(
    private val stringProvider: StringProvider
) {
    fun getCalendarSpecification(dateOfFirstRegistration: LocalDate?): SpecificationIconUiModel {
        val year = dateOfFirstRegistration?.toYearDisplayFormat() ?: "Unknown"
        return SpecificationIconUiModel(
            icon = R.drawable.ic_calendar,
            description = AccessibleString(
                displayText = year,
                altText = stringProvider.getString(
                    R.string.first_registered_in_alt_text,
                    year
                )
            )
        )
    }

    fun getFuelTypeSpecification(fuelType: FuelType): SpecificationIconUiModel {
        val fuelType = fuelType.getResources()
        val fuelName = stringProvider.getString(fuelType.second)
        return SpecificationIconUiModel(
            icon = fuelType.first,
            description = AccessibleString(
                displayText = fuelName,
                altText = stringProvider.getString(R.string.fuel_type_alt_text, fuelName)
            )
        )
    }

    fun getColourSpecification(colour: VehicleColour): SpecificationIconUiModel {
        val colour = stringProvider.getString(colour.getResource())
        return SpecificationIconUiModel(
            icon = R.drawable.ic_colour,
            description = AccessibleString(
                displayText = colour,
                altText = stringProvider.getString(R.string.colour_alt_text, colour)
            )
        )
    }

    fun createMakeItem(vehicle: VehicleDetails): InternalLinkListItemModel.Info =
        InternalLinkListItemModel.Info(
            title = AccessibleString(displayText = stringProvider.getString(R.string.make_title)),
            info = AccessibleString(displayText = vehicle.summary.make)
        )

    fun createModelItem(vehicle: VehicleDetails): InternalLinkListItemModel.Info =
        InternalLinkListItemModel.Info(
            title = AccessibleString(displayText = stringProvider.getString(R.string.model_title)),
            info = AccessibleString(displayText = vehicle.summary.model ?: "Unknown")
        )

    fun createFirstRegisteredItem(vehicle: VehicleDetails): InternalLinkListItemModel.Info {
        val dateOfFirstRegistration =
            vehicle.dateOfFirstRegistration?.toMonthYearDisplayFormat() ?: "Unknown"

        return InternalLinkListItemModel.Info(
            title = AccessibleString(
                displayText = stringProvider.getString(R.string.first_registered_title),
                altText = stringProvider.getString(
                    R.string.first_registered_alt_text,
                    dateOfFirstRegistration
                )
            ),
            info = AccessibleString(
                displayText = dateOfFirstRegistration,
                altText = "" // Set as empty string so nothing read as alt text handled in the title
            )
        )
    }

    fun createFuelTypeItem(vehicle: VehicleDetails): InternalLinkListItemModel.Info =
        InternalLinkListItemModel.Info(
            title = AccessibleString(displayText = stringProvider.getString(R.string.fuel_type_title)),
            info = AccessibleString(
                displayText = stringProvider.getString(vehicle.fuelType.getResources().third)
            )
        )

    fun createColourItem(vehicle: VehicleDetails): InternalLinkListItemModel.Info =
        InternalLinkListItemModel.Info(
            title = AccessibleString(displayText = stringProvider.getString(R.string.colour_title)),
            info = AccessibleString(displayText = vehicle.getVehicleColour(stringProvider))
        )

    fun createEngineSizeItem(vehicle: VehicleDetails): InternalLinkListItemModel.Info =
        InternalLinkListItemModel.Info(
            title = AccessibleString(displayText = stringProvider.getString(R.string.engine_size_title)),
            info = AccessibleString(
                displayText = vehicle.engineCapacity?.let { getFormattedEngineCapacity(it) }
                    ?: "Unknown"
            )
        )

    fun createEmissionsItem(vehicle: VehicleDetails): InternalLinkListItemModel.Info =
        InternalLinkListItemModel.Info(
            title = AccessibleString(displayText = stringProvider.getString(R.string.emissions_title)),
            info = AccessibleString(
                displayText = vehicle.exhaustEmissionsCo2?.toString() ?: "Unknown"
            )
        )
}
