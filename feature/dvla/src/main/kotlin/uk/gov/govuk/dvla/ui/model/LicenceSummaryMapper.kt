package uk.gov.govuk.dvla.ui.model

import uk.gov.govuk.config.data.remote.model.DvlaUrls
import uk.gov.govuk.design.ui.model.AccessibleString
import uk.gov.govuk.design.ui.model.StatusListItemIconStyle
import uk.gov.govuk.dvla.R
import uk.gov.govuk.dvla.domain.DriverSummary
import uk.gov.govuk.dvla.domain.LicenceStatus
import uk.gov.govuk.dvla.domain.LicenceType
import uk.gov.govuk.dvla.util.StringProvider
import uk.gov.govuk.dvla.util.getNumberOfDaysWithinDayRangeAsPercentage
import uk.gov.govuk.dvla.util.getNumberOfDaysFromNow
import uk.gov.govuk.dvla.util.isDateWithinDayRange
import uk.gov.govuk.dvla.util.isToday
import uk.gov.govuk.dvla.util.resolveSummaryDescription
import uk.gov.govuk.dvla.util.toSummaryDisplayFormat
import uk.gov.govuk.dvla.util.toTitleCase
import java.time.LocalDate
import javax.inject.Inject

internal class LicenceSummaryMapper @Inject constructor(
    private val stringProvider: StringProvider
) {
    private companion object {
        const val UPPER_RANGE_OF_EXPIRY_DAYS = 56
    }

    fun toUiModel(driverSummary: DriverSummary, dvlaUrls: DvlaUrls?): LicenceSummaryUiModel {
        return LicenceSummaryUiModel(
            licenceType = getLicenceTypeString(driverSummary.licenceType),
            licenceNumber = driverSummary.licenceNumber,
            name = driverSummary.fullName.toTitleCase(),
            addressLine1 = driverSummary.addressLine1.toTitleCase(),
            city = driverSummary.addressLine5.toTitleCase(),
            postcode = driverSummary.postcode.uppercase(),
            statusUi = getLicenceStatusUiModel(
                status = driverSummary.status,
                expiryDate = driverSummary.expiryDate,
                dvlaUrls = dvlaUrls
            ),
            menuItems = buildMenuItems(
                licenceNumber = driverSummary.licenceNumber,
                dvlaUrls = dvlaUrls
            )
        )
    }

    private fun getLicenceStatusUiModel(
        status: LicenceStatus,
        expiryDate: LocalDate?,
        dvlaUrls: DvlaUrls?
    ) = when (status) {
        LicenceStatus.EXPIRED -> getExpired(expiryDate, dvlaUrls)
        LicenceStatus.VALID -> {
            if (expiryDate?.isDateWithinDayRange(UPPER_RANGE_OF_EXPIRY_DAYS) == true) {
                getExpiring(expiryDate, dvlaUrls)
            } else {
                getValid(expiryDate)
            }
        }
        // TODO: temporary, other states to be added on future tickets
        else -> getValid(expiryDate)
    }

    private fun getValid(expiryDate: LocalDate?): StatusUiModel {
        val expiryDate = expiryDate?.toSummaryDisplayFormat()
        return StatusUiModel.StatusRow(
            statusRowUi = StatusRowUiModel(
                description = AccessibleString(displayText = expiryDate?.let {
                    stringProvider.resolveSummaryDescription(
                        R.string.valid_until,
                        expiryDate
                    )
                } ?: run { stringProvider.getString(R.string.valid) }),
                iconStyle = StatusListItemIconStyle.Success
            )
        )
    }

    private fun getExpiring(expiryDate: LocalDate, dvlaUrls: DvlaUrls?): StatusUiModel {
        val formattedExpiryDate = expiryDate.toSummaryDisplayFormat()
        return StatusUiModel.CountdownRow(
            countdownBarUi = StatusCountdownUiModel(
                topText = AccessibleString(
                    displayText = stringProvider.resolveSummaryDescription(
                        R.string.expiring_status_date,
                        formattedExpiryDate
                    ),
                    altText = stringProvider.resolveSummaryDescription(
                        R.string.expiring_licence_date_alt_text,
                        formattedExpiryDate
                    )
                ),
                percentage = expiryDate.getNumberOfDaysWithinDayRangeAsPercentage(
                    UPPER_RANGE_OF_EXPIRY_DAYS
                ),
                bottomText = AccessibleString(
                    displayText = getExpiringBottomText(expiryDate)
                ),
                style = getLicenceExpiringStyle(dvlaUrls)
            )
        )
    }

    private fun getExpiringBottomText(expiryDate: LocalDate) =
        if (expiryDate.isToday()) {
            stringProvider.getString(R.string.today)
        } else {
            val numberOfDaysFromNow = expiryDate.getNumberOfDaysFromNow()
            stringProvider.getQuantityString(
                R.plurals.expiring_status_days_left,
                numberOfDaysFromNow,
                numberOfDaysFromNow
            )
        }

    private fun getExpired(expiryDate: LocalDate?, dvlaUrls: DvlaUrls?): StatusUiModel {
        val expiryDate = expiryDate?.toSummaryDisplayFormat() ?: ""
        val description = stringProvider.resolveSummaryDescription(
            R.string.expired_on,
            expiryDate
        )
        return StatusUiModel.StatusRow(
            statusRowUi = StatusRowUiModel(
                description = AccessibleString(
                    displayText = description,
                    altText = stringProvider.getString(
                        R.string.licence_expiration_alt_text,
                        description
                    )
                ),
                iconStyle = StatusListItemIconStyle.Warning,
                style = getLicenceExpiringStyle(dvlaUrls)
            )
        )
    }

    private fun getLicenceExpiringStyle(dvlaUrls: DvlaUrls?) = dvlaUrls?.renewLicence?.let { taxVehicleUrl ->
        StatusStyle.ActionButton(
            text = AccessibleString(stringProvider.getString(R.string.renew_licence_button)),
            url = UrlModel(taxVehicleUrl),
            caption = AccessibleString(displayText = stringProvider.getString(R.string.renew_licence_caption))
        )
    }

    private fun buildMenuItems(licenceNumber: String, dvlaUrls: DvlaUrls?): List<OverflowMenuItem> {
        dvlaUrls ?: return emptyList()
        return buildList {
            add(
                OverflowMenuItem(
                    text = AccessibleString(
                        stringProvider.getString(R.string.menu_copy_licence_number)
                    ),
                    action = MenuAction.ClipboardCopy(licenceNumber)
                )
            )
            add(
                OverflowMenuItem(
                    text = AccessibleString(
                        stringProvider.getString(R.string.menu_change_licence_address),
                        stringProvider.getString(R.string.menu_change_licence_address_alt_text)
                    ),
                    action = MenuAction.WebLink(dvlaUrls.changeLicenceAddress)
                )
            )
            add(
                OverflowMenuItem(
                    text = AccessibleString(
                        stringProvider.getString(R.string.menu_change_licence_name_gender),
                        stringProvider.getString(R.string.menu_change_licence_name_gender_alt_text)
                    ),
                    action = MenuAction.WebLink(dvlaUrls.changeNameGenderLicence)
                )
            )
            add(
                OverflowMenuItem(
                    text = AccessibleString(
                        stringProvider.getString(R.string.menu_replace_licence)
                    ),
                    action = MenuAction.WebLink(dvlaUrls.replaceLicence)
                )
            )
        }
    }

    private fun getLicenceTypeString(type: LicenceType): String =
        when (type) {
            LicenceType.FULL -> stringProvider.getString(R.string.licence_type_full)
            LicenceType.PROVISIONAL -> stringProvider.getString(R.string.licence_type_provisional)
            LicenceType.UNKNOWN -> "Unknown"    // TODO return unknown for now, other states in future tickets
        }
}
