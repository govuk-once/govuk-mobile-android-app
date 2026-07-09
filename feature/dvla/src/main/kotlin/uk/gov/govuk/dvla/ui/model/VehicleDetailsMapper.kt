package uk.gov.govuk.dvla.ui.model

import uk.gov.govuk.config.data.remote.model.DvlaUrls
import uk.gov.govuk.design.ui.model.AccessibleString
import uk.gov.govuk.design.ui.model.InternalLinkListItemModel
import uk.gov.govuk.design.ui.model.SpecificationIconUiModel
import uk.gov.govuk.dvla.util.StringProvider
import uk.gov.govuk.dvla.R
import uk.gov.govuk.dvla.domain.FuelType
import uk.gov.govuk.dvla.domain.FuelType.DIESEL
import uk.gov.govuk.dvla.domain.FuelType.ELECTRICITY
import uk.gov.govuk.dvla.domain.FuelType.ELECTRIC_DIESEL
import uk.gov.govuk.dvla.domain.FuelType.FUEL_CELLS
import uk.gov.govuk.dvla.domain.FuelType.GAS
import uk.gov.govuk.dvla.domain.FuelType.GAS_BI_FUEL
import uk.gov.govuk.dvla.domain.FuelType.GAS_DIESEL
import uk.gov.govuk.dvla.domain.FuelType.HYBRID_ELECTRIC
import uk.gov.govuk.dvla.domain.FuelType.OTHER
import uk.gov.govuk.dvla.domain.FuelType.PETROL
import uk.gov.govuk.dvla.domain.FuelType.PETROL_GAS
import uk.gov.govuk.dvla.domain.FuelType.STEAM
import uk.gov.govuk.dvla.domain.VehicleColour
import uk.gov.govuk.dvla.domain.VehicleColour.BEIGE
import uk.gov.govuk.dvla.domain.VehicleColour.BLACK
import uk.gov.govuk.dvla.domain.VehicleColour.BLUE
import uk.gov.govuk.dvla.domain.VehicleColour.BRONZE
import uk.gov.govuk.dvla.domain.VehicleColour.BROWN
import uk.gov.govuk.dvla.domain.VehicleColour.CREAM
import uk.gov.govuk.dvla.domain.VehicleColour.GOLD
import uk.gov.govuk.dvla.domain.VehicleColour.GREEN
import uk.gov.govuk.dvla.domain.VehicleColour.GREY
import uk.gov.govuk.dvla.domain.VehicleColour.MAROON
import uk.gov.govuk.dvla.domain.VehicleColour.MULTI_COLOUR
import uk.gov.govuk.dvla.domain.VehicleColour.NOT_STATED
import uk.gov.govuk.dvla.domain.VehicleColour.ORANGE
import uk.gov.govuk.dvla.domain.VehicleColour.PINK
import uk.gov.govuk.dvla.domain.VehicleColour.PURPLE
import uk.gov.govuk.dvla.domain.VehicleColour.RED
import uk.gov.govuk.dvla.domain.VehicleColour.SILVER
import uk.gov.govuk.dvla.domain.VehicleColour.TURQUOISE
import uk.gov.govuk.dvla.domain.VehicleColour.WHITE
import uk.gov.govuk.dvla.domain.VehicleColour.YELLOW
import uk.gov.govuk.dvla.domain.VehicleDetails
import uk.gov.govuk.dvla.util.getFormattedEngineCapacity
import uk.gov.govuk.dvla.util.getFormattedEngineCapacityAltText
import uk.gov.govuk.dvla.util.toYearDisplayFormat
import javax.inject.Inject

internal class VehicleDetailsMapper @Inject constructor(
    private val stringProvider: StringProvider,
    private val taxAndMotStatusMapper: TaxAndMotStatusMapper
) {
    fun toUiModel(vesVehicle: VehicleDetails, dvlaUrls: DvlaUrls?): VehicleDetailsUiModel {
        val engineCapacity =
            vesVehicle.engineCapacity?.let { getFormattedEngineCapacity(it) } ?: "Unknown"
        val yearOfFirstRegistration =
            vesVehicle.dateOfFirstRegistration?.toYearDisplayFormat() ?: "Unknown"
        return VehicleDetailsUiModel(
            make = vesVehicle.summary.make,
            model = vesVehicle.summary.model ?: "Unknown", // TODO: no requirement for null model yet
            registration = vesVehicle.summary.registration,
            keeper = vesVehicle.getKeeper(),
            specificationsIcons = listOf(
                vesVehicle.getCalendarSpecification(),
                vesVehicle.getFuelTypeSpecification(),
                vesVehicle.getColourSpecification()
            ),
            taxStatus = taxAndMotStatusMapper.getTaxStatus(vesVehicle.summary, dvlaUrls),
            motStatus = taxAndMotStatusMapper.getMotStatus(vesVehicle.summary, dvlaUrls),
            specifications = listOf(
                InternalLinkListItemModel.Info(
                    title = AccessibleString(displayText = stringProvider.getString(R.string.make_title)),
                    info = AccessibleString(displayText = vesVehicle.summary.make)
                ),
                InternalLinkListItemModel.Info(
                    title = AccessibleString(displayText = stringProvider.getString(R.string.model_title)),
                    info = AccessibleString(displayText = vesVehicle.summary.model ?: "Unknown")
                ),
                InternalLinkListItemModel.Info(
                    title = AccessibleString(
                        displayText = stringProvider.getString(R.string.first_registered_title),
                        altText = stringProvider.getString(
                            R.string.first_registered_alt_text,
                            yearOfFirstRegistration
                        )
                    ),
                    info = AccessibleString(
                        displayText = yearOfFirstRegistration,
                        altText = "" // Set as empty string so nothing read as alt text handled in the title
                    )
                ),
                InternalLinkListItemModel.Info(
                    title = AccessibleString(displayText = stringProvider.getString(R.string.fuel_type_title)),
                    info = AccessibleString(
                        displayText = stringProvider.getString(vesVehicle.fuelType.getResources().third)
                    )
                ),
                InternalLinkListItemModel.Info(
                    title = AccessibleString(displayText = stringProvider.getString(R.string.colour_title)),
                    info = AccessibleString(displayText = vesVehicle.getVehicleColour())
                ),
                InternalLinkListItemModel.Info(
                    title = AccessibleString(displayText = stringProvider.getString(R.string.engine_size_title)),
                    info = AccessibleString(
                        displayText = engineCapacity,
                        altText = getFormattedEngineCapacityAltText(
                            engineCapacity,
                            replacementText = stringProvider.getString(R.string.litres_alt_text)
                        )
                    )
                ),
                InternalLinkListItemModel.Info(
                    title = AccessibleString(displayText = stringProvider.getString(R.string.emissions_title)),
                    info = AccessibleString(
                        displayText = vesVehicle.exhaustEmissionsCo2?.let {
                            stringProvider.getString(R.string.emissions_info, it)
                        } ?: "Unknown",
                        altText = vesVehicle.exhaustEmissionsCo2?.let {
                            stringProvider.getString(R.string.emissions_alt_text, it)
                        } ?: "Unknown")
                )
            )
        )
    }

    private fun VehicleDetails.getCalendarSpecification(): SpecificationIconUiModel {
        val year = this.dateOfFirstRegistration?.toYearDisplayFormat() ?: "Unknown"
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

    private fun VehicleDetails.getFuelTypeSpecification(): SpecificationIconUiModel {
        val fuelType = this.fuelType.getResources()
        val fuelName = stringProvider.getString(fuelType.second)
        return SpecificationIconUiModel(
            icon = fuelType.first,
            description = AccessibleString(
                displayText = fuelName,
                altText = stringProvider.getString(R.string.fuel_type_alt_text, fuelName)
            )
        )
    }

    private fun VehicleDetails.getColourSpecification(): SpecificationIconUiModel {
        val colour = stringProvider.getString(this.colour.getResource())
        return SpecificationIconUiModel(
            icon = R.drawable.ic_colour,
            description = AccessibleString(
                displayText = colour,
                altText = stringProvider.getString(R.string.colour_alt_text, colour)
            )
        )
    }

    private fun VehicleDetails.getVehicleColour(): String {
        val colourRes = stringProvider.getString(this.colour.getResource())
        return this.secondaryColour?.let { secondaryColour ->
            val secondaryColourRes = stringProvider.getString(secondaryColour.getResource())
            stringProvider.getString(
                R.string.concatenated_vehicle_colours,
                colourRes, secondaryColourRes.lowercase()
            )
        } ?: run { colourRes }
    }

    private fun VehicleDetails.getKeeper(): KeeperUiModel {
        val name = listOfNotNull(
            this.keeperTitle,
            this.keeperFirstNames,
            this.keeperLastName
        ).joinToString(separator = " ")

        val addressLines = this.keeperFullAddress
            ?.split("\n")
            ?.filter { it.isNotBlank() }
            ?: emptyList()

        return KeeperUiModel(name = name, addressLines = addressLines)
    }

    private fun FuelType.getResources() = when (this) {
        PETROL -> Triple(
            R.drawable.ic_petrol_diesel,
            R.string.petrol_summary,
            R.string.petrol_specification
        )

        DIESEL -> Triple(
            R.drawable.ic_petrol_diesel,
            R.string.diesel_summary,
            R.string.diesel_specification
        )

        ELECTRICITY -> Triple(
            R.drawable.ic_electric,
            R.string.electric_summary,
            R.string.electric_specification
        )

        STEAM -> Triple(
            R.drawable.ic_steam,
            R.string.steam_summary,
            R.string.steam_specification
        )

        GAS -> Triple(
            R.drawable.ic_gas,
            R.string.gas_summary,
            R.string.gas_specification
        )

        PETROL_GAS -> Triple(
            R.drawable.ic_petrol_diesel,
            R.string.petrol_gas_summary,
            R.string.petrol_gas_specification
        )

        GAS_BI_FUEL -> Triple(
            R.drawable.ic_petrol_diesel,
            R.string.gas_bi_fuel_summary,
            R.string.gas_bi_fuel_specification
        )

        HYBRID_ELECTRIC -> Triple(
            R.drawable.ic_hybrid,
            R.string.hybrid_electric_summary,
            R.string.hybrid_electric_specification
        )

        GAS_DIESEL -> Triple(
            R.drawable.ic_petrol_diesel,
            R.string.gas_diesel_summary,
            R.string.gas_diesel_specification
        )

        FUEL_CELLS -> Triple(
            R.drawable.ic_petrol_diesel,
            R.string.fuel_cells_summary,
            R.string.fuel_cells_specification
        )

        ELECTRIC_DIESEL -> Triple(
            R.drawable.ic_petrol_diesel,
            R.string.electric_diesel_summary,
            R.string.electric_diesel_specification
        )

        OTHER -> Triple(
            R.drawable.ic_petrol_diesel,
            R.string.other,
            R.string.other
        )
    }

    private fun VehicleColour.getResource() = when (this) {
        BROWN -> R.string.brown
        BRONZE -> R.string.bronze
        RED -> R.string.red
        PINK -> R.string.pink
        ORANGE -> R.string.orange
        YELLOW -> R.string.yellow
        GOLD -> R.string.gold
        GREEN -> R.string.green
        BLUE -> R.string.blue
        PURPLE -> R.string.purple
        GREY -> R.string.grey
        SILVER -> R.string.silver
        WHITE -> R.string.white
        BLACK -> R.string.black
        MULTI_COLOUR -> R.string.multi_colour
        BEIGE -> R.string.beige
        MAROON -> R.string.maroon
        TURQUOISE -> R.string.turquoise
        CREAM -> R.string.cream
        NOT_STATED -> R.string.not_stated
        else -> R.string.not_stated
    }
}
