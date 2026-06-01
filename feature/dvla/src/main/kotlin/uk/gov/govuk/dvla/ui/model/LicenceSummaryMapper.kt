package uk.gov.govuk.dvla.ui.model

import uk.gov.govuk.dvla.R
import uk.gov.govuk.dvla.domain.DriverSummary
import uk.gov.govuk.dvla.domain.LicenceStatus
import uk.gov.govuk.dvla.domain.LicenceType
import uk.gov.govuk.dvla.util.StringProvider
import uk.gov.govuk.dvla.util.resolveSummaryDescription
import uk.gov.govuk.dvla.util.toSummaryDisplayFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

internal class LicenceSummaryMapper @Inject constructor(
    private val stringProvider: StringProvider
) {

    fun toUiModel(driverSummary: DriverSummary): LicenceSummaryUiModel {
        val formattedExpiryDate = driverSummary.expiryDate?.toSummaryDisplayFormat()

        val (statusStringResId, statusIconResId) = getLicenceStatusResources(driverSummary.status)

        return LicenceSummaryUiModel(
            licenceType = getLicenceTypeString(driverSummary.licenceType),
            licenceNumber = driverSummary.licenceNumber,
            name = driverSummary.fullName,
            addressLine1 = driverSummary.addressLine1,
            city = driverSummary.addressLine5,
            postcode = driverSummary.postcode,
            licenceStatus = StatusRowUiModel(
                description = stringProvider.resolveSummaryDescription(statusStringResId, formattedExpiryDate),
                icon = statusIconResId
            )
        )
    }

    private fun getLicenceTypeString(type: LicenceType): String =
        when (type) {
            LicenceType.FULL -> stringProvider.getString(R.string.licence_type_full)
            LicenceType.PROVISIONAL -> stringProvider.getString(R.string.licence_type_provisional)
            LicenceType.UNKNOWN -> "Unknown"    // TODO return unknown for now, other states in future tickets
        }

    private fun getLicenceStatusResources(status: LicenceStatus): Pair<Int?, Int?> =
        when(status) {
            LicenceStatus.VALID -> Pair(
                R.string.valid_until,
                uk.gov.govuk.design.R.drawable.ic_check_round
            )

            else -> Pair(null, null)
        }
}
