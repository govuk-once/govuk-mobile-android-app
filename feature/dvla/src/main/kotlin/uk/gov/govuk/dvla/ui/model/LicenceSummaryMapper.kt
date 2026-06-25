package uk.gov.govuk.dvla.ui.model

import uk.gov.govuk.design.ui.model.AccessibleString
import uk.gov.govuk.design.ui.model.ProgressBarListItemUiModel
import uk.gov.govuk.design.ui.model.StatusListItemIconStyle
import uk.gov.govuk.dvla.R
import uk.gov.govuk.dvla.domain.DriverSummary
import uk.gov.govuk.dvla.domain.LicenceStatus
import uk.gov.govuk.dvla.domain.LicenceType
import uk.gov.govuk.dvla.util.StringProvider
import uk.gov.govuk.dvla.util.getNumberOfDaysUntilExpiryAsPercentage
import uk.gov.govuk.dvla.util.getNumberOfDaysFromNow
import uk.gov.govuk.dvla.util.isLicenceExpiring
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
        const val DAYS_UNTIL_LICENCE_EXPIRY = 56
    }

    fun toUiModel(driverSummary: DriverSummary) = LicenceSummaryUiModel(
        licenceType = getLicenceTypeString(driverSummary.licenceType),
        licenceNumber = driverSummary.licenceNumber,
        name = driverSummary.fullName.toTitleCase(),
        addressLine1 = driverSummary.addressLine1.toTitleCase(),
        city = driverSummary.addressLine5.toTitleCase(),
        postcode = driverSummary.postcode.uppercase(),
        statusUi = getLicenceStatusUiModel(
            status = driverSummary.status,
            expiryDate = driverSummary.expiryDate
        )
    )

    private fun getLicenceStatusUiModel(
        status: LicenceStatus,
        expiryDate: LocalDate?
    ): LicenceStatusUiModel {
        val formattedExpiryDate = expiryDate?.toSummaryDisplayFormat() ?: "Unknown"
        return when (status) {
            LicenceStatus.EXPIRED -> getExpired(formattedExpiryDate)
            LicenceStatus.VALID -> {
                if (expiryDate?.isLicenceExpiring(DAYS_UNTIL_LICENCE_EXPIRY) == true) {
                    getExpiring(expiryDate)
                } else {
                    getValid(formattedExpiryDate)
                }
            }
            // TODO: temporary, other states to be added on future tickets
            else -> getValid(formattedExpiryDate)
        }
    }

    private fun getValid(expiryDate: String) = LicenceStatusUiModel.Valid(
        statusRowUi = StatusRowUiModel(
            description = stringProvider.resolveSummaryDescription(
                R.string.valid_until,
                expiryDate
            ),
            iconStyle = StatusListItemIconStyle.Success
        )
    )

    private fun getExpiring(expiryDate: LocalDate): LicenceStatusUiModel.Expiring {
        val formattedExpiryDate = expiryDate.toSummaryDisplayFormat()
        return LicenceStatusUiModel.Expiring(
            progressBarUi = ProgressBarListItemUiModel(
                topText = AccessibleString(
                    displayText = stringProvider.resolveSummaryDescription(
                        R.string.expiring_licence_date,
                        formattedExpiryDate
                    ),
                    altText = stringProvider.resolveSummaryDescription(
                        R.string.expiring_licence_date_alt_text,
                        formattedExpiryDate
                    )
                ),
                percentage = expiryDate.getNumberOfDaysUntilExpiryAsPercentage(
                    DAYS_UNTIL_LICENCE_EXPIRY
                ),
                bottomText = AccessibleString(
                    displayText = getExpiringBottomText(expiryDate)
                )
            )
        )
    }

    private fun getExpiringBottomText(expiryDate: LocalDate): String {
        return if (expiryDate.isToday()) {
            stringProvider.getString(R.string.today)
        } else {
            stringProvider.getQuantityString(
                R.plurals.expiring_licence_days_left,
                expiryDate.getNumberOfDaysFromNow(),
                expiryDate.getNumberOfDaysFromNow()
            )
        }
    }

    private fun getExpired(expiryDate: String) = LicenceStatusUiModel.Expired(
        statusRowUi = StatusRowUiModel(
            description = stringProvider.resolveSummaryDescription(
                R.string.expired_on,
                expiryDate
            ),
            iconStyle = StatusListItemIconStyle.Success
        )
    )

    private fun getLicenceTypeString(type: LicenceType): String =
        when (type) {
            LicenceType.FULL -> stringProvider.getString(R.string.licence_type_full)
            LicenceType.PROVISIONAL -> stringProvider.getString(R.string.licence_type_provisional)
            LicenceType.UNKNOWN -> "Unknown"    // TODO return unknown for now, other states in future tickets
        }
}
