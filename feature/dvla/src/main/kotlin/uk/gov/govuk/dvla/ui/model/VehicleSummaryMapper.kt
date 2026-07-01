package uk.gov.govuk.dvla.ui.model

import uk.gov.govuk.config.data.ConfigRepo
import uk.gov.govuk.design.ui.model.AccessibleString
import uk.gov.govuk.design.ui.model.StatusListItemIconStyle
import uk.gov.govuk.dvla.util.StringProvider
import uk.gov.govuk.dvla.R
import uk.gov.govuk.dvla.domain.CustomerVehicle
import uk.gov.govuk.dvla.domain.TaxStatus
import uk.gov.govuk.dvla.util.getNumberOfDaysFromNow
import uk.gov.govuk.dvla.util.getNumberOfDaysWithinDayRangeAsPercentage
import uk.gov.govuk.dvla.util.isDateWithinDayRange
import uk.gov.govuk.dvla.util.isDirectDebit
import uk.gov.govuk.dvla.util.isInThePast
import uk.gov.govuk.dvla.util.isToday
import uk.gov.govuk.dvla.util.resolveSummaryDescription
import uk.gov.govuk.dvla.util.toSummaryDisplayFormat
import java.time.LocalDate
import javax.inject.Inject

internal class VehicleSummaryMapper @Inject constructor(
    private val stringProvider: StringProvider,
    private val configRepo: ConfigRepo
) {
    private companion object {
        const val UPPER_RANGE_OF_TAX_EXPIRY_DAYS = 28
    }

    fun toUiModel(vehicle: CustomerVehicle): VehicleSummaryUiModel {
        return VehicleSummaryUiModel(
            registration = vehicle.registration,
            make = vehicle.make,
            model = vehicle.model
                ?: "Unknown", // TODO return unknown for now, other states in future tickets
            taxStatus = getTaxStatus(vehicle = vehicle),
            motStatus = getMotStatus(vehicle = vehicle)
        )
    }

    private fun getMotStatus(vehicle: CustomerVehicle): StatusUiModel {
        when (vehicle.taxStatus) {
            TaxStatus.SORN -> return StatusUiModel.NoStatus
            else -> { /* Do nothing */ }
        }
        // TODO: MOT states in future ticket
        return getValid(getMotStatusTitle(), vehicle.motExpiryDate)
    }

    private fun getTaxStatus(vehicle: CustomerVehicle): StatusUiModel {
        val expiryDate = vehicle.taxExpiryDate
        return when (vehicle.taxStatus) {
            TaxStatus.TAXED -> {
                if (expiryDate?.isDateWithinDayRange(UPPER_RANGE_OF_TAX_EXPIRY_DAYS) == true) {
                    if (vehicle.isPaymentMethodDirectDebit()) {
                        getTaxExpiringDirectDebit(expiryDate)
                    } else {
                        getTaxExpiring(expiryDate)
                    }
                } else {
                    getValid(getTaxStatusTitle(), expiryDate)
                }
            }
            TaxStatus.UNTAXED -> getTaxExpired(expiryDate)
            TaxStatus.SORN -> getSorn(vehicle.sornStart)
            TaxStatus.NOT_TAXED_FOR_ON_ROAD_USE -> getNotNeeded(getTaxStatusTitle())
            TaxStatus.UNKNOWN -> getUnknown(getTaxStatusTitle())
        }
    }

    private fun CustomerVehicle.isPaymentMethodDirectDebit() =
        this.currentLicence?.paymentMethod.isDirectDebit()

    private fun getValid(title: AccessibleString, expiryDate: LocalDate?): StatusUiModel {
        val resources =
            Triple(R.string.valid_until, R.string.valid, StatusListItemIconStyle.Success)
        return getStatusRow(title, expiryDate, resources)
    }

    private fun getExpiringTopText(expiryDate: String) = AccessibleString(
        displayText = stringProvider.resolveSummaryDescription(
            R.string.expiring_status_date,
            expiryDate
        )
    )

    private fun getExpiringDirectDebitTopText(expiryDate: String) = AccessibleString(
        displayText = stringProvider.resolveSummaryDescription(
            R.string.expiring_direct_debit_status_date,
            expiryDate
        )
    )

    private fun getUnknown(title: AccessibleString) = StatusUiModel.StatusRow(
        statusRowUi = StatusRowUiModel(
            title = title,
            description = AccessibleString(stringProvider.getString(R.string.status_unknown)),
            iconStyle = null
        )
    )

    private fun getNotNeeded(title: AccessibleString) = StatusUiModel.StatusRow(
        statusRowUi = StatusRowUiModel(
            title = title,
            description = AccessibleString(stringProvider.getString(R.string.status_not_needed)),
            iconStyle = null
        )
    )

    private fun getStatusRow(
        title: AccessibleString,
        expiryDate: LocalDate?,
        resources: Triple<Int?, Int, StatusListItemIconStyle?>,
        style: StatusStyle? = null
    ): StatusUiModel {
        val expiryDate = expiryDate?.toSummaryDisplayFormat()
        return StatusUiModel.StatusRow(
            statusRowUi = StatusRowUiModel(
                title = title,
                description = AccessibleString(
                    displayText = expiryDate?.let {
                        stringProvider.resolveSummaryDescription(
                            resources.first,
                            expiryDate
                        )
                    } ?: run { stringProvider.getString(resources.second) }
                ),
                iconStyle = resources.third,
                style = style
            )
        )
    }

    private fun getExpiringBottomText(expiryDate: LocalDate) =
        AccessibleString(
            displayText = if (expiryDate.isToday()) {
                stringProvider.getString(R.string.today)
            } else {
                val numberOfDaysFromNow = expiryDate.getNumberOfDaysFromNow()
                stringProvider.getQuantityString(
                    R.plurals.expiring_status_days_left,
                    numberOfDaysFromNow,
                    numberOfDaysFromNow
                )
            }
        )


    /* Tax specific functions START */

    private fun getTaxExpiring(expiryDate: LocalDate): StatusUiModel {
        val formattedExpiryDate = expiryDate.toSummaryDisplayFormat()
        return StatusUiModel.CountdownRow(
            countdownBarUi = StatusCountdownUiModel(
                topText = getExpiringTopText(formattedExpiryDate),
                percentage = expiryDate.asPercentageOfDaysLeftForTax(),
                bottomText = getExpiringBottomText(expiryDate),
                title = getTaxStatusTitle(),
                style = getTaxExpiringStyle()
            )
        )
    }

    private fun getTaxExpiringStyle() = configRepo.dvlaUrls?.taxVehicle?.let { taxVehicleUrl ->
        StatusStyle.ActionButton(
            text = AccessibleString(stringProvider.getString(R.string.renew_tax_button)),
            url = taxVehicleUrl,
            caption = AccessibleString(stringProvider.getString(R.string.renew_tax_button_caption))
        )
    }

    private fun getTaxExpiringDirectDebit(expiryDate: LocalDate): StatusUiModel {
        val formattedExpiryDate = expiryDate.toSummaryDisplayFormat()
        return StatusUiModel.CountdownRow(
            countdownBarUi = StatusCountdownUiModel(
                topText = getExpiringDirectDebitTopText(formattedExpiryDate),
                percentage = expiryDate.asPercentageOfDaysLeftForTax(),
                bottomText = AccessibleString(stringProvider.getString(R.string.paying_by_direct_debit)),
                title = getTaxStatusTitle(),
                style = getExpiringDirectDebitStyle()
            )
        )
    }

    private fun LocalDate.asPercentageOfDaysLeftForTax() = this.getNumberOfDaysWithinDayRangeAsPercentage(
        UPPER_RANGE_OF_TAX_EXPIRY_DAYS
    )

    private fun getExpiringDirectDebitStyle() =
        configRepo.dvlaUrls?.manageTaxPayment?.let { manageTaxPaymentUrl ->
            StatusStyle.ActionButton(
                text = AccessibleString(stringProvider.getString(R.string.manage_payment_button)),
                url = manageTaxPaymentUrl,
                isPrimary = false
            )
        }

    private fun getTaxExpired(expiryDate: LocalDate?): StatusUiModel {
        val resources =
            Triple(R.string.expired_on, R.string.expired, StatusListItemIconStyle.Warning)
        return getStatusRow(getTaxStatusTitle(), expiryDate, resources, getTaxExpiringStyle())
    }

    private fun getSorn(sornStart: LocalDate?): StatusUiModel {
        val subtitle =
            if (sornStart?.isInThePast() == true) null else AccessibleString(
                stringProvider.resolveSummaryDescription(
                    R.string.sorn_from,
                    sornStart?.toSummaryDisplayFormat()
                )
            )
        return StatusUiModel.InfoRow(
            InfoRowUiModel(
                title = AccessibleString(stringProvider.getString(R.string.off_the_road_sorn_message)),
                subtitle = subtitle,
                icon = R.drawable.ic_circle_p
            )
        )
    }

    private fun getTaxStatusTitle() = AccessibleString(
        displayText = stringProvider.getString(R.string.tax_status_title)
    )

    /* Tax specific functions END */

    /* MOT specific functions START */

    private fun getMotStatusTitle() = AccessibleString(
        displayText = stringProvider.getString(R.string.acronym_mot),
        altText = stringProvider.getString(R.string.acronym_mot_alt_text)
    )

    /* MOT specific functions END */
}
