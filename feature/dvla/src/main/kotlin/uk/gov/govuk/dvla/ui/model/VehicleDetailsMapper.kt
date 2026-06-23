package uk.gov.govuk.dvla.ui.model

import uk.gov.govuk.design.ui.model.AccessibleString
import uk.gov.govuk.design.ui.model.InternalLinkListItemModel
import uk.gov.govuk.design.ui.model.SpecificationIconUiModel
import uk.gov.govuk.design.ui.model.StatusListItemIconStyle
import uk.gov.govuk.dvla.util.StringProvider
import uk.gov.govuk.dvla.R
import uk.gov.govuk.dvla.domain.CustomerVehicle
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
import uk.gov.govuk.dvla.domain.MotStatus
import uk.gov.govuk.dvla.domain.TaxStatus
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
import uk.gov.govuk.dvla.util.getFormattedEngineCapacity
import uk.gov.govuk.dvla.util.getFormattedEngineCapacityAltText
import uk.gov.govuk.dvla.util.resolveSummaryDescription
import uk.gov.govuk.dvla.util.toSummaryDisplayFormat
import uk.gov.govuk.dvla.util.toYearDisplayFormat
import javax.inject.Inject

internal class VehicleDetailsMapper @Inject constructor(
    private val stringProvider: StringProvider
) {
    // TODO change param to VesVehicle when details endpoint live
    fun toUiModel(vesVehicle: CustomerVehicle): VehicleDetailsUiModel {
        val engineCapacity =
            vesVehicle.engineCapacity?.let { getFormattedEngineCapacity(it) } ?: "Unknown"
        return VehicleDetailsUiModel(
            make = vesVehicle.make,
            model = vesVehicle.model ?: "Unknown", // TODO: no requirement for null model yet
            registration = vesVehicle.registration,
            keeper = vesVehicle.getKeeper(),
            specificationsIcons = listOf(
                vesVehicle.getCalendarSpecification(),
                vesVehicle.getFuelTypeSpecification(),
                vesVehicle.getColourSpecification()
            ),
            taxStatus = vesVehicle.getTaxRow(),
            motStatus = vesVehicle.getMotRow(),
            specifications = listOf(
                InternalLinkListItemModel(
                    title = stringProvider.getString(R.string.make_title),
                    info = AccessibleString(displayText = vesVehicle.make)
                ),
                InternalLinkListItemModel(
                    title = stringProvider.getString(R.string.model_title),
                    info = AccessibleString(displayText = vesVehicle.model ?: "Unknown")
                ),
                InternalLinkListItemModel(
                    title = stringProvider.getString(R.string.first_registered_title),
                    info = vesVehicle.getDateOfFirstRegistration()
                ),
                InternalLinkListItemModel(
                    title = stringProvider.getString(R.string.fuel_type_title),
                    info = AccessibleString(
                        displayText = stringProvider.getString(vesVehicle.fuelType.getResources().third)
                    )
                ),
                InternalLinkListItemModel(
                    title = stringProvider.getString(R.string.colour_title),
                    info = AccessibleString(displayText = vesVehicle.getVehicleColour())
                ),
                InternalLinkListItemModel(
                    title = stringProvider.getString(R.string.engine_size_title),
                    info = AccessibleString(
                        displayText = engineCapacity,
                        altText = getFormattedEngineCapacityAltText(
                            engineCapacity,
                            replacementText = stringProvider.getString(R.string.litres_alt_text)
                        )
                    )
                ),
                InternalLinkListItemModel(
                    title = stringProvider.getString(R.string.emissions_title),
                    info = AccessibleString(
                        displayText = vesVehicle.exhaustEmissions?.co2?.let {
                            stringProvider.getString(R.string.emissions_info, it)
                        } ?: "Unknown",
                        altText = vesVehicle.exhaustEmissions?.co2?.let {
                            stringProvider.getString(R.string.emissions_alt_text, it)
                        } ?: "Unknown")
                )
            )
        )
    }

    private fun CustomerVehicle.getDateOfFirstRegistration(): AccessibleString {
        val year = this.dateOfFirstRegistration?.toYearDisplayFormat() ?: "Unknown"
        return AccessibleString(
            displayText = year,
            altText = stringProvider.getString(
                R.string.first_registered_in_alt_text,
                year
            )
        )
    }

    private fun CustomerVehicle.getTaxRow(): StatusRowUiModel {
        val taxDate = this.taxExpiryDate?.toSummaryDisplayFormat()
        val (taxStringResId, iconStyle) = this.taxStatus.getResources()
        return StatusRowUiModel(
            title = stringProvider.getString(R.string.tax_status_title),
            description = stringProvider.resolveSummaryDescription(taxStringResId, taxDate),
            iconStyle = iconStyle
        )
    }

    private fun TaxStatus.getResources() = when (this) {
        TaxStatus.TAXED -> Pair(
            R.string.valid_until,
            StatusListItemIconStyle.Success
        )

        else -> Pair(null, null)
    }

    private fun CustomerVehicle.getMotRow(): StatusRowUiModel {
        val motDate = this.motExpiryDate?.toSummaryDisplayFormat()
        val (motStringResId, iconStyle) = this.motStatus.getResources()

        return StatusRowUiModel(
            title = stringProvider.getString(R.string.acronym_mot),
            titleAltText = stringProvider.getString(R.string.acronym_mot_alt_text),
            description = stringProvider.resolveSummaryDescription(motStringResId, motDate),
            iconStyle = iconStyle
        )
    }

    private fun MotStatus.getResources() = when (this) {
        MotStatus.VALID -> Pair(
            R.string.valid_until,
            StatusListItemIconStyle.Success
        )

        else -> Pair(null, null)
    }

    private fun CustomerVehicle.getCalendarSpecification() =
        SpecificationIconUiModel(
            icon = R.drawable.ic_calendar,
            description = this.getDateOfFirstRegistration()
        )

    private fun CustomerVehicle.getFuelTypeSpecification(): SpecificationIconUiModel {
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

    private fun CustomerVehicle.getColourSpecification(): SpecificationIconUiModel {
        val colour = stringProvider.getString(this.colour.getResource())
        return SpecificationIconUiModel(
            icon = R.drawable.ic_colour,
            description = AccessibleString(
                displayText = colour,
                altText = stringProvider.getString(R.string.colour_alt_text, colour)
            )
        )
    }

    private fun CustomerVehicle.getVehicleColour(): String {
        val colourRes = stringProvider.getString(this.colour.getResource())
        return this.secondaryColour?.let { secondaryColour ->
            val secondaryColourRes = stringProvider.getString(secondaryColour.getResource())
            stringProvider.getString(
                R.string.concatenated_vehicle_colours,
                colourRes, secondaryColourRes.lowercase()
            )
        } ?: run { colourRes }
    }

    // TODO DVLA are working on sending keeper formatted so below is for demo only
    private fun CustomerVehicle.getKeeper() = KeeperUiModel(
        "${this.keeper?.title ?: ""} ${this.keeper?.firstNames ?: ""} ${this.keeper?.lastName ?: ""}",
        "29 Orchard Drive",
        "Milton Keynes",
        "PA98 J83"
    )

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

        GAS -> Triple(R.drawable.ic_gas,
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
