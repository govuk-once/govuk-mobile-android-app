package uk.gov.govuk.dvla.ui.model

import uk.gov.govuk.design.ui.model.InternalLinkListItemModel
import uk.gov.govuk.design.ui.model.SpecificationIconUiModel
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
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

internal class VehicleDetailsMapper @Inject constructor(
    private val stringProvider: StringProvider
) {
    private companion object {
        const val DATE_FORMAT_d_MMMM_yyyy = "d MMMM yyyy"
        const val DATE_FORMAT_YYYY = "YYYY"
    }

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
                    title = stringProvider.getString(R.string.make_title), info = vesVehicle.make
                ),
                InternalLinkListItemModel(
                    title = stringProvider.getString(R.string.model_title),
                    info = vesVehicle.model ?: "Unknown"
                ),
                InternalLinkListItemModel(
                    title = stringProvider.getString(R.string.first_registered_title),
                    info = vesVehicle.getDateOfFirstRegistration(),
                    altText = stringProvider.getString(
                        R.string.first_registered_in_alt_text,
                        vesVehicle.getDateOfFirstRegistration()
                    )
                ),
                InternalLinkListItemModel(
                    title = stringProvider.getString(R.string.fuel_type_title),
                    info = stringProvider.getString(vesVehicle.fuelType.getResources().second)
                ),
                InternalLinkListItemModel(
                    title = stringProvider.getString(R.string.colour_title),
                    info = vesVehicle.getVehicleColour()
                ),
                InternalLinkListItemModel(
                    title = stringProvider.getString(R.string.engine_size_title),
                    info = engineCapacity,
                    altText = getFormattedEngineCapacityAltText(
                        engineCapacity,
                        replacementText = stringProvider.getString(R.string.litres_alt_text)
                    )
                ),
                InternalLinkListItemModel(
                    title = stringProvider.getString(R.string.emissions_title),
                    info = vesVehicle.exhaustEmissions?.co2?.let {
                        stringProvider.getString(R.string.emissions_info, it)
                    } ?: "Unknown",
                    altText = vesVehicle.exhaustEmissions?.co2?.let {
                        stringProvider.getString(R.string.emissions_alt_text, it)
                    } ?: "Unknown"
                )
            )
        )
    }

    private fun CustomerVehicle.getDateOfFirstRegistration() =
        this.dateOfFirstRegistration?.toDisplayFormat(
            DATE_FORMAT_YYYY
        ) ?: "Unknown"

    private fun CustomerVehicle.getTaxRow(): StatusRowUiModel {
        val taxDate = this.taxExpiryDate?.toDisplayFormat(DATE_FORMAT_d_MMMM_yyyy)
        val (taxStringResId, taxIconResId) = getTaxStatusResources(this.taxStatus)
        return StatusRowUiModel(
            title = stringProvider.getString(R.string.tax_status_title),
            description = stringProvider.resolveSummaryDescription(taxStringResId, taxDate),
            icon = taxIconResId
        )
    }

    private fun CustomerVehicle.getMotRow(): StatusRowUiModel {
        val motDate = this.motExpiryDate?.toDisplayFormat(DATE_FORMAT_d_MMMM_yyyy)
        val (motStringResId, motIconResId) = getMotStatusResources(this.motStatus)

        return StatusRowUiModel(
            title = stringProvider.getString(R.string.acronym_mot),
            titleAltText = stringProvider.getString(R.string.acronym_mot_alt_text),
            description = stringProvider.resolveSummaryDescription(motStringResId, motDate),
            icon = motIconResId
        )
    }

    private fun CustomerVehicle.getCalendarSpecification(): SpecificationIconUiModel {
        val registrationDate =
            this.dateOfFirstRegistration?.toDisplayFormat(DATE_FORMAT_YYYY) ?: ""
        return SpecificationIconUiModel(
            icon = R.drawable.ic_calendar,
            description = registrationDate,
            altText = stringProvider.getString(
                R.string.first_registered_in_alt_text,
                registrationDate
            )
        )
    }

    private fun CustomerVehicle.getFuelTypeSpecification(): SpecificationIconUiModel {
        val fuelType = this.fuelType.getResources()
        val fuelName = stringProvider.getString(fuelType.second)
        return SpecificationIconUiModel(
            icon = fuelType.first,
            description = fuelName,
            altText = stringProvider.getString(R.string.fuel_type_alt_text, fuelName)
        )
    }

    private fun CustomerVehicle.getColourSpecification(): SpecificationIconUiModel {
        val colour = this.getVehicleColour()
        return SpecificationIconUiModel(
            icon = R.drawable.ic_colour,
            description = colour,
            altText = stringProvider.getString(R.string.colour_alt_text, colour)
        )
    }

    private fun CustomerVehicle.getVehicleColour(): String {
        val colour = stringProvider.getString(this.colour.getResource())
        return this.secondaryColour?.let { secondaryColour ->
            val secondaryColour = stringProvider.getString(secondaryColour.getResource())
            stringProvider.getString(
                R.string.concatenated_vehicle_colours,
                colour,
                secondaryColour.lowercase()
            )
        } ?: colour
    }

    // TODO what if date is null?
    private fun getTaxStatusResources(status: TaxStatus): Pair<Int?, Int?> =
        when (status) {
            TaxStatus.TAXED -> Pair(
                R.string.valid_until,
                uk.gov.govuk.design.R.drawable.ic_check_round
            )

            else -> Pair(null, null)
        }

    // TODO what if date is null?
    private fun getMotStatusResources(status: MotStatus): Pair<Int?, Int?> =
        when (status) {
            MotStatus.VALID -> Pair(
                R.string.valid_until,
                uk.gov.govuk.design.R.drawable.ic_check_round
            )

            else -> Pair(null, null)
        }

    // TODO DVLA are working on sending keeper formatted so below is for demo only
    private fun CustomerVehicle.getKeeper() = KeeperUiModel(
        "${this.keeper?.title ?: ""} ${this.keeper?.firstNames ?: ""} ${this.keeper?.lastName ?: ""}",
        "Address",
        "City",
        "Postcode"
    )

    private fun FuelType.getResources() = when (this) {
        PETROL -> Pair(R.drawable.ic_petrol_diesel, R.string.petrol)
        DIESEL -> Pair(R.drawable.ic_petrol_diesel, R.string.diesel)
        ELECTRICITY -> Pair(R.drawable.ic_electric, R.string.electric)
        STEAM -> Pair(R.drawable.ic_steam, R.string.steam)
        GAS -> Pair(R.drawable.ic_gas, R.string.gas)
        PETROL_GAS -> Pair(R.drawable.ic_petrol_diesel, R.string.petrol_and_gas)
        GAS_BI_FUEL -> Pair(R.drawable.ic_petrol_diesel, R.string.gas_bi_fuel)
        HYBRID_ELECTRIC -> Pair(R.drawable.ic_hybrid, R.string.hybrid_electric)
        GAS_DIESEL -> Pair(R.drawable.ic_petrol_diesel, R.string.gas_diesel)
        FUEL_CELLS -> Pair(R.drawable.ic_petrol_diesel, R.string.fuel_cells)
        ELECTRIC_DIESEL -> Pair(R.drawable.ic_petrol_diesel, R.string.electric_diesel)
        OTHER -> Pair(R.drawable.ic_petrol_diesel, R.string.other)
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
    }

    private fun LocalDate.toDisplayFormat(pattern: String): String =
        runCatching { this.format(DateTimeFormatter.ofPattern(pattern)) }.getOrDefault("")
}
