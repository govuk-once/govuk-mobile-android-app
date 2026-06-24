package uk.gov.govuk.dvla.ui.model

import uk.gov.govuk.design.ui.model.AccessibleString
import uk.gov.govuk.design.ui.model.StatusListItemIconStyle
import uk.gov.govuk.dvla.util.StringProvider
import uk.gov.govuk.dvla.R
import uk.gov.govuk.dvla.domain.CustomerVehicle
import uk.gov.govuk.dvla.domain.MotStatus
import uk.gov.govuk.dvla.domain.TaxStatus
import uk.gov.govuk.dvla.util.resolveSummaryDescription
import uk.gov.govuk.dvla.util.toSummaryDisplayFormat
import javax.inject.Inject

internal class VehicleSummaryMapper @Inject constructor(
    private val stringProvider: StringProvider
) {
    fun toUiModel(vehicle: CustomerVehicle): VehicleSummaryUiModel {
        val taxDate = vehicle.taxExpiryDate?.toSummaryDisplayFormat()
        val motDate = vehicle.motExpiryDate?.toSummaryDisplayFormat()

        val (taxStringResId, taxIconStyle) = getTaxStatusResources(vehicle.taxStatus)
        val (motStringResId, motIconStyle) = getMotStatusResources(vehicle.motStatus, vehicle.taxStatus)

        val taxDescriptionText = stringProvider.resolveSummaryDescription(taxStringResId, taxDate)
        val motDescriptionText = stringProvider.resolveSummaryDescription(motStringResId, motDate)

        return VehicleSummaryUiModel(
            registration = vehicle.registration,
            make = vehicle.make,
            model = vehicle.model ?: "Unknown", // TODO return unknown for now, other states in future tickets
            taxStatus = StatusRowUiModel(
                title = AccessibleString(
                    displayText = stringProvider.getString(R.string.tax_status_title)
                ),
                description = AccessibleString(
                    displayText = taxDescriptionText
                ),
                iconStyle = taxIconStyle
            ),
            motStatus = StatusRowUiModel(
                title = AccessibleString(
                    displayText = stringProvider.getString(R.string.acronym_mot),
                    altText = stringProvider.getString(R.string.acronym_mot_alt_text)
                ),
                description = AccessibleString(
                    displayText = motDescriptionText
                ),
                iconStyle = motIconStyle
            )
        )
    }

    // TODO what if date is null?
    private fun getTaxStatusResources(status: TaxStatus): Pair<Int?, StatusListItemIconStyle?> =
        when (status) {
            TaxStatus.TAXED -> Pair(R.string.valid_until, StatusListItemIconStyle.Success)
            else -> Pair(null, null)
        }

    // TODO what if date is null?
    private fun getMotStatusResources(motStatus: MotStatus, taxStatus: TaxStatus): Pair<Int?, StatusListItemIconStyle?> =
        when {
            // exempt
            motStatus == MotStatus.NOT_VALID && taxStatus == TaxStatus.SORN -> Pair(R.string.exempt, null)

            // standard states
            motStatus == MotStatus.VALID -> Pair(R.string.valid_until, StatusListItemIconStyle.Success)
            motStatus == MotStatus.NOT_VALID -> Pair(R.string.expired_on, StatusListItemIconStyle.Warning)
            else -> Pair(null, null)
        }
}

