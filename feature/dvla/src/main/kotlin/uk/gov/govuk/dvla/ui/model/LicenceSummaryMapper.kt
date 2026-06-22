package uk.gov.govuk.dvla.ui.model

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

    fun toUiModel(driverSummary: DriverSummary): LicenceSummaryUiModel {
        val formattedExpiryDate = driverSummary.expiryDate?.toSummaryDisplayFormat()
        val (statusStringResId, statusIconStyle) = getLicenceStatusResources(driverSummary.status)
        val descriptionText = stringProvider.resolveSummaryDescription(statusStringResId, formattedExpiryDate)
        val descriptionAltText = stringProvider.getString(R.string.licence_expiration_alt_text, descriptionText)
        val actionUiModel = getLicenceActionUiModel(driverSummary.status)

        return LicenceSummaryUiModel(
            licenceType = getLicenceTypeString(driverSummary.licenceType),
            licenceNumber = driverSummary.licenceNumber,
            name = driverSummary.fullName.toTitleCase(),
            addressLine1 = driverSummary.addressLine1.toTitleCase(),
            city = driverSummary.addressLine5.toTitleCase(),
            postcode = driverSummary.postcode.uppercase(),
            statusRowUi = StatusRowUiModel(
                description = AccessibleString(
                    displayText = descriptionText,
                    altText = descriptionAltText
                ),
                iconStyle = statusIconStyle,
                action = actionUiModel
            )
        )
    }

    private fun getLicenceActionUiModel(status: LicenceStatus): StatusActionUiModel? =
        when (status) {
            LicenceStatus.EXPIRED -> StatusActionUiModel(
                buttonText = stringProvider.getString(R.string.renew_licence_button),
                caption = stringProvider.getString(R.string.renew_licence_caption)
            )
            else -> null
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
