package uk.gov.govuk.dvla.ui.model

import uk.gov.govuk.config.data.remote.model.DvlaUrls
import uk.gov.govuk.design.ui.model.AccessibleString
import uk.gov.govuk.design.ui.model.StatusListItemIconStyle
import uk.gov.govuk.dvla.R
import uk.gov.govuk.dvla.domain.MotStatus
import uk.gov.govuk.dvla.domain.TaxStatus
import uk.gov.govuk.dvla.domain.VehicleSummary
import uk.gov.govuk.dvla.util.StringProvider
import uk.gov.govuk.dvla.util.getNumberOfDaysFromNow
import uk.gov.govuk.dvla.util.getNumberOfDaysWithinDayRangeAsPercentage
import uk.gov.govuk.dvla.util.insertRegistration
import uk.gov.govuk.dvla.util.isDateWithinDayRange
import uk.gov.govuk.dvla.util.isDirectDebit
import uk.gov.govuk.dvla.util.isInTheFuture
import uk.gov.govuk.dvla.util.isToday
import uk.gov.govuk.dvla.util.resolveSummaryDescription
import uk.gov.govuk.dvla.util.toSummaryDisplayFormat
import java.time.LocalDate
import javax.inject.Inject

internal class TaxAndMotStatusMapper @Inject constructor(
    private val stringProvider: StringProvider
) {
    private companion object {
        const val UPPER_RANGE_OF_TAX_EXPIRY_DAYS = 28
        const val UPPER_RANGE_OF_MOT_EXPIRY_DAYS = 28
    }

    fun getMotStatus(vehicle: VehicleSummary, dvlaUrls: DvlaUrls?): StatusUiModel {
        val expiryDate = vehicle.motExpiryDate
        return when (vehicle.motStatus) {
            MotStatus.VALID -> {
                if (expiryDate?.isDateWithinDayRange(UPPER_RANGE_OF_MOT_EXPIRY_DAYS) == true) {
                    getMotExpiring(expiryDate)
                } else {
                    getValid(getMotStatusTitle(), expiryDate)
                }
            }

            MotStatus.EXPIRED -> getMotExpired(expiryDate)
            MotStatus.NO_DETAILS_HELD -> {
                dvlaUrls?.checkMot?.let { checkMotUrl ->
                    getNoMotDetailsHeld(checkMotUrl, vehicle.registration)
                } ?: run {
                    getUnknown(getMotStatusTitle())
                }
            }

            MotStatus.NO_RESULTS_RETURNED -> {
                dvlaUrls?.historicVehicles?.let { historicVehiclesUrl ->
                    getNoMotResultsReturned(historicVehiclesUrl)
                } ?: run {
                    getUnknown(getMotStatusTitle())
                }
            }

            MotStatus.UNKNOWN -> getUnknown(getMotStatusTitle())
        }
    }

    fun getTaxStatus(vehicle: VehicleSummary, dvlaUrls: DvlaUrls?): StatusUiModel {
        val expiryDate = vehicle.taxExpiryDate
        return when (vehicle.taxStatus) {
            TaxStatus.TAXED -> {
                if (vehicle.sornStart?.isInTheFuture() == true) {
                    getSorn(vehicle.sornStart)
                } else if (expiryDate?.isDateWithinDayRange(UPPER_RANGE_OF_TAX_EXPIRY_DAYS) == true) {
                    if (vehicle.currentLicencePaymentMethod.isDirectDebit()) {
                        getTaxExpiringDirectDebit(expiryDate, dvlaUrls)
                    } else {
                        getTaxExpiring(expiryDate, dvlaUrls)
                    }
                } else {
                    getValid(getTaxStatusTitle(), expiryDate)
                }
            }

            TaxStatus.UNTAXED -> getTaxExpired(expiryDate, dvlaUrls)
            TaxStatus.SORN -> getSorn(vehicle.sornStart)
            TaxStatus.NOT_TAXED_FOR_ON_ROAD_USE -> getNotNeeded(getTaxStatusTitle())
            TaxStatus.UNKNOWN -> getUnknown(getTaxStatusTitle())
        }
    }

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

    private fun getTaxExpiring(expiryDate: LocalDate, dvlaUrls: DvlaUrls?): StatusUiModel {
        val formattedExpiryDate = expiryDate.toSummaryDisplayFormat()
        return StatusUiModel.CountdownRow(
            countdownBarUi = StatusCountdownUiModel(
                topText = getExpiringTopText(formattedExpiryDate),
                percentage = expiryDate.asPercentageOfDaysLeftForTax(),
                bottomText = getExpiringBottomText(expiryDate),
                title = getTaxStatusTitle(),
                style = getTaxExpiringStyle(dvlaUrls)
            )
        )
    }

    private fun getTaxExpiringStyle(dvlaUrls: DvlaUrls?) =
        dvlaUrls?.taxVehicle?.let { taxVehicleUrl ->
            StatusStyle.ActionButton(
                text = AccessibleString(stringProvider.getString(R.string.renew_tax_button)),
                url = UrlModel(taxVehicleUrl),
                caption = AccessibleString(stringProvider.getString(R.string.renew_tax_button_caption))
            )
        }

    private fun getTaxExpiringDirectDebit(
        expiryDate: LocalDate,
        dvlaUrls: DvlaUrls?
    ): StatusUiModel {
        val formattedExpiryDate = expiryDate.toSummaryDisplayFormat()
        return StatusUiModel.CountdownRow(
            countdownBarUi = StatusCountdownUiModel(
                topText = getExpiringDirectDebitTopText(formattedExpiryDate),
                percentage = expiryDate.asPercentageOfDaysLeftForTax(),
                bottomText = AccessibleString(stringProvider.getString(R.string.paying_by_direct_debit)),
                title = getTaxStatusTitle(),
                style = getExpiringDirectDebitStyle(dvlaUrls)
            )
        )
    }

    private fun LocalDate.asPercentageOfDaysLeftForTax() =
        this.getNumberOfDaysWithinDayRangeAsPercentage(
            UPPER_RANGE_OF_TAX_EXPIRY_DAYS
        )

    private fun getExpiringDirectDebitStyle(dvlaUrls: DvlaUrls?) =
        dvlaUrls?.manageTaxPayment?.let { manageTaxPaymentUrl ->
            StatusStyle.ActionButton(
                text = AccessibleString(stringProvider.getString(R.string.manage_payment_button)),
                caption = AccessibleString(stringProvider.getString(R.string.manage_payment_button_caption)),
                url = UrlModel(manageTaxPaymentUrl),
                isPrimary = false
            )
        }

    private fun getTaxExpired(expiryDate: LocalDate?, dvlaUrls: DvlaUrls?): StatusUiModel {
        val resources =
            Triple(R.string.expired_on, R.string.expired, StatusListItemIconStyle.Warning)
        return getStatusRow(
            getTaxStatusTitle(),
            expiryDate,
            resources,
            getTaxExpiringStyle(dvlaUrls)
        )
    }

    private fun getSorn(sornStart: LocalDate?): StatusUiModel {
        val subtitle = if (sornStart.isInTheFuture()) {
            AccessibleString(
                stringProvider.resolveSummaryDescription(
                    R.string.sorn_from,
                    sornStart?.toSummaryDisplayFormat()
                )
            )
        } else {
            null
        }
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

    private fun getMotExpiring(expiryDate: LocalDate): StatusUiModel {
        val formattedExpiryDate = expiryDate.toSummaryDisplayFormat()
        return StatusUiModel.CountdownRow(
            countdownBarUi = StatusCountdownUiModel(
                topText = getExpiringTopText(formattedExpiryDate),
                percentage = expiryDate.asPercentageOfDaysLeftForMot(),
                bottomText = getExpiringBottomText(expiryDate),
                title = getMotStatusTitle(),
                style = StatusStyle.Caption(
                    text = AccessibleString(
                        displayText = stringProvider.getString(R.string.expiring_mot_caption),
                        altText = stringProvider.getString(R.string.expiring_mot_caption_alt_text)
                    )
                )
            )
        )
    }

    private fun getMotExpired(expiryDate: LocalDate?): StatusUiModel {
        val resources =
            Triple(R.string.expired_on, R.string.expired, StatusListItemIconStyle.Warning)
        return getStatusRow(getMotStatusTitle(), expiryDate, resources)
    }

    private fun getMotStatusTitle() = AccessibleString(
        displayText = stringProvider.getString(R.string.acronym_mot),
        altText = stringProvider.getString(R.string.acronym_mot_alt_text)
    )

    private fun LocalDate.asPercentageOfDaysLeftForMot() =
        this.getNumberOfDaysWithinDayRangeAsPercentage(
            UPPER_RANGE_OF_MOT_EXPIRY_DAYS
        )

    private fun getNoMotDetailsHeld(url: String, registration: String): StatusUiModel {
        val formattedUrl = url.insertRegistration(registration)
        return StatusUiModel.LinkRow(
            linkRowUi = LinkRowUiModel(
                title = getMotStatusTitle(),
                text = AccessibleString(stringProvider.getString(R.string.no_details_held_link_text)),
                url = UrlModel(originalUrl = url, formattedUrl = formattedUrl)
            )
        )
    }

    private fun getNoMotResultsReturned(url: String) = StatusUiModel.LinkRow(
        linkRowUi = LinkRowUiModel(
            title = getMotStatusTitle(),
            text = AccessibleString(
                displayText = stringProvider.getString(R.string.check_mot_link_text),
                altText = stringProvider.getString(R.string.check_mot_link_alt_text)
            ),
            url = UrlModel(url)
        )
    )

    /* MOT specific functions END */
}