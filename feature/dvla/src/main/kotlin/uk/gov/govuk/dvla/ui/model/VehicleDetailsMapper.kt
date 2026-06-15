package uk.gov.govuk.dvla.ui.model

import uk.gov.govuk.design.ui.model.InternalLinkListItemModel
import uk.gov.govuk.design.ui.model.SpecificationUiModel
import uk.gov.govuk.dvla.util.StringProvider
import uk.gov.govuk.dvla.R
import uk.gov.govuk.dvla.domain.CustomerVehicle
import uk.gov.govuk.dvla.domain.MotStatus
import uk.gov.govuk.dvla.domain.TaxStatus
import uk.gov.govuk.dvla.domain.VehicleSummary
import uk.gov.govuk.dvla.util.resolveSummaryDescription
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

internal class VehicleDetailsMapper @Inject constructor(
    private val stringProvider: StringProvider
) {
    private companion object {
        const val DATE_FORMAT_d_MMMM_yyyy = "d-MMMM-yyyy"
        const val DATE_FORMAT_YYYY = "YYYY"
    }

    // TODO change param to VesVehicle when details endpoint live
    fun toDetailsUiModel(vesVehicle: CustomerVehicle): VehicleDetailsUiModel {
        return VehicleDetailsUiModel(
            make = vesVehicle.make,
            model = vesVehicle.model ?: "Unknown", // TODO: no requirement for null model yet
            registration = vesVehicle.registration,
            specifications = listOf(
                vesVehicle.getCalendarSpecification(),
                vesVehicle.getFuelTypeSpecification(),
                vesVehicle.getColourSpecification()
            ),
            taxStatus = getTaxRow(vesVehicle),
            motStatus = getMotRow(vesVehicle),
            extraDetails = listOf(
                InternalLinkListItemModel(
                    title = stringProvider.getString(R.string.make_title), info = vesVehicle.make
                ),
                InternalLinkListItemModel(
                    title = stringProvider.getString(R.string.model_title),
                    info = vesVehicle.model ?: "Unknown"
                ),
                InternalLinkListItemModel(
                    title = stringProvider.getString(R.string.first_registered_title),
                    info = vesVehicle.getDateOfFirstRegistration()
                ),
                InternalLinkListItemModel(
                    title = stringProvider.getString(R.string.fuel_type_title),
                    info = stringProvider.getString(vesVehicle.fuelType.getResources().label)
                ),
                InternalLinkListItemModel(
                    title = stringProvider.getString(R.string.colour_title),
                    info = vesVehicle.getColour()
                ),
                InternalLinkListItemModel(
                    title = stringProvider.getString(R.string.engine_size_title),
                    info = vesVehicle.getEngineSize()
                ),
                InternalLinkListItemModel(
                    title = stringProvider.getString(R.string.emissions_title),
                    info = vesVehicle.euroStatus ?: "Unknown",
                    infoAltText = vesVehicle.getEmissionsAltText()
                )
            )
        )
    }

    fun toUiModel(vehicle: CustomerVehicle): VehicleSummaryUiModel = VehicleSummaryUiModel(
        registration = vehicle.registration,
        make = vehicle.make,
        model = vehicle.model
            ?: "Unknown", // TODO return unknown for now, other states in future tickets
        taxStatus = getTaxRow(vehicle),
        motStatus = getMotRow(vehicle)
    )

    private fun CustomerVehicle.getDateOfFirstRegistration() =
        this.dateOfFirstRegistration?.toDisplayFormat(
            DATE_FORMAT_YYYY
        ) ?: "Unknown"

    private fun CustomerVehicle.getEngineSize(): String {
        val capacity = this.engineCapacity ?: return "Unknown"
        if (capacity < 1000) return "${capacity}cc"
        val capacityInLitres = "${capacity / 100 / 10.0}L"
        return capacityInLitres
    }

    private fun CustomerVehicle.getColour(): String {
        val colour = stringProvider.getString(this.colour.getResource())
        this.secondaryColour?.let { secondaryColour ->
            val and = stringProvider.getString(R.string.and)
            val secondaryColour = stringProvider.getString(secondaryColour.getResource())
            return "$colour $and $secondaryColour"
        }
        return colour
    }

    private fun getTaxRow(vehicle: VehicleSummary): StatusRowUiModel {
        val taxDate = vehicle.taxExpiryDate?.toDisplayFormat(DATE_FORMAT_d_MMMM_yyyy)
        val (taxStringResId, taxIconResId) = getTaxStatusResources(vehicle.taxStatus)
        return StatusRowUiModel(
            title = stringProvider.getString(R.string.tax_status_title),
            description = stringProvider.resolveSummaryDescription(taxStringResId, taxDate),
            icon = taxIconResId
        )
    }

    private fun getMotRow(vehicle: VehicleSummary): StatusRowUiModel {
        val motDate = vehicle.motExpiryDate?.toDisplayFormat(DATE_FORMAT_d_MMMM_yyyy)
        val (motStringResId, motIconResId) = getMotStatusResources(vehicle.motStatus)

        return StatusRowUiModel(
            title = stringProvider.getString(R.string.acronym_mot),
            titleAltText = stringProvider.getString(R.string.acronym_mot_alt_text),
            description = stringProvider.resolveSummaryDescription(motStringResId, motDate),
            icon = motIconResId
        )
    }

    private fun CustomerVehicle.getCalendarSpecification(): SpecificationUiModel {
        val registrationDate =
            this.dateOfFirstRegistration?.toDisplayFormat(DATE_FORMAT_YYYY) ?: ""
        return SpecificationUiModel(
            icon = R.drawable.ic_calendar,
            description = registrationDate,
            altText = stringProvider.getString(
                R.string.first_registered_in_alt_text,
                registrationDate
            )
        )
    }

    private fun CustomerVehicle.getFuelTypeSpecification(): SpecificationUiModel {
        val fuelType = this.fuelType.getResources()
        return SpecificationUiModel(
            icon = fuelType.icon,
            description = stringProvider.getString(fuelType.label),
            altText = stringProvider.getString(R.string.fuel_type_alt_text, fuelType)
        )
    }

    private fun CustomerVehicle.getColourSpecification() = SpecificationUiModel(
        icon = R.drawable.ic_colour,
        description = stringProvider.getString(this.colour.getResource()),
        altText = stringProvider.getString(R.string.colour_alt_text, this.colour)
    )

    private fun CustomerVehicle.getEmissionsAltText() : String {
        this.euroStatus ?: return "Unknown"
        return this.euroStatus.replace("g/km", stringProvider.getString(R.string.emissions_alt_text))
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

    private fun LocalDate.toDisplayFormat(pattern: String): String =
        runCatching { this.format(DateTimeFormatter.ofPattern(pattern)) }.getOrDefault("")
}
