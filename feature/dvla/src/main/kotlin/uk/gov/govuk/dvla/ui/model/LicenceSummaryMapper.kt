package uk.gov.govuk.dvla.ui.model

import uk.gov.govuk.config.data.remote.model.DvlaUrls
import uk.gov.govuk.design.ui.model.AccessibleString
import uk.gov.govuk.design.ui.model.StatusListItemIconStyle
import uk.gov.govuk.dvla.R
import uk.gov.govuk.dvla.domain.DriverSummary
import uk.gov.govuk.dvla.domain.LicenceStatus
import uk.gov.govuk.dvla.domain.LicenceType
import uk.gov.govuk.dvla.util.StringProvider
import uk.gov.govuk.dvla.util.resolveSummaryDescription
import uk.gov.govuk.dvla.util.toSummaryDisplayFormat
import uk.gov.govuk.dvla.util.toTitleCase
import javax.inject.Inject

internal class LicenceSummaryMapper @Inject constructor(
    private val stringProvider: StringProvider
) {

    fun toUiModel(driverSummary: DriverSummary, dvlaUrls: DvlaUrls?): LicenceSummaryUiModel {
        val formattedExpiryDate = driverSummary.expiryDate?.toSummaryDisplayFormat()

        val (statusStringResId, statusIconStyle) = getLicenceStatusResources(driverSummary.status)

        return LicenceSummaryUiModel(
            licenceType = getLicenceTypeString(driverSummary.licenceType),
            licenceNumber = driverSummary.licenceNumber,
            name = driverSummary.fullName.toTitleCase(),
            addressLine1 = driverSummary.addressLine1.toTitleCase(),
            city = driverSummary.addressLine5.toTitleCase(),
            postcode = driverSummary.postcode.uppercase(),
            status = driverSummary.status,
            statusRowUi = StatusRowUiModel(
                description = stringProvider.resolveSummaryDescription(statusStringResId, formattedExpiryDate),
                iconStyle = statusIconStyle
            ),
            menuItems = buildMenuItems(licenceNumber = driverSummary.licenceNumber, dvlaUrls = dvlaUrls)
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

    private fun getLicenceStatusResources(status: LicenceStatus): Pair<Int?, StatusListItemIconStyle?> =
        when (status) {
            LicenceStatus.VALID -> Pair(R.string.valid_until, StatusListItemIconStyle.Success)
            LicenceStatus.EXPIRED -> Pair(R.string.expired_on, StatusListItemIconStyle.Warning)
            else -> Pair(null, null)
        }
}
