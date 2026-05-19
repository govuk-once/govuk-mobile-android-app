package uk.gov.govuk.dvla.ui.model

import uk.gov.govuk.dvla.util.StringProvider
import uk.gov.govuk.dvla.R
import uk.gov.govuk.dvla.domain.CustomerVehicle
import uk.gov.govuk.dvla.domain.MotStatus
import uk.gov.govuk.dvla.domain.TaxStatus
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

internal class VehicleSummaryMapper @Inject constructor(
    private val stringProvider: StringProvider
) {
    fun toUiModel(vehicle: CustomerVehicle): VehicleSummaryUiModel {
        val taxDate = vehicle.taxExpiryDate?.toSummaryDisplayFormat()
        val motDate = vehicle.motExpiryDate?.toSummaryDisplayFormat()

        val (taxStringResId, taxIconResId) = getTaxStatusResources(vehicle.taxStatus)
        val (motStringResId, motIconResId) = getMotStatusResources(vehicle.motStatus)

        return VehicleSummaryUiModel(
            registration = vehicle.registration,
            make = vehicle.make,
            model = vehicle.model ?: "Unknown", // TODO return unknown for now, other states in future tickets
            taxStatus = StatusRowUiModel(
                title = stringProvider.getString(R.string.tax_status_title),
                description = resolveDescription(taxStringResId, taxDate),
                icon = taxIconResId
            ),
            motStatus = StatusRowUiModel(
                title = stringProvider.getString(R.string.acronym_mot),
                titleAltText = stringProvider.getString(R.string.acronym_mot_alt_text),
                description = resolveDescription(motStringResId, motDate),
                icon = motIconResId
            )
        )
    }

    private fun resolveDescription(resId: Int?, dateArg: String?): String {
        val id = resId ?: return "Unknown" // TODO return unknown for now, other states in future tickets
        return dateArg?.let { stringProvider.getString(id, it) }
            ?: stringProvider.getString(id)
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
}

private val dvlaDateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")

private fun LocalDate.toSummaryDisplayFormat(): String = this.format(dvlaDateFormatter)
