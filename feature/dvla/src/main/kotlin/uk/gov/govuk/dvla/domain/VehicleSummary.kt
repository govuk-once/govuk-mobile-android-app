package uk.gov.govuk.dvla.domain

import uk.gov.govuk.data.extension.toLocalDateOrNull
import uk.gov.govuk.dvla.remote.model.VehicleSummary as RemoteVehicleSummary
import java.time.LocalDate

data class VehicleSummary(
    val registration: String,
    val make: String,
    val model: String?,
    val taxStatus: TaxStatus,
    val taxExpiryDate: LocalDate?,
    val motStatus: MotStatus,
    val motExpiryDate: LocalDate?,
    val sornStart: LocalDate?,
    val currentLicencePaymentMethod: String?
)

internal fun RemoteVehicleSummary.toDomainModel(): VehicleSummary {
    return VehicleSummary(
        registration = this.registrationNumber,
        make = this.make,
        model = this.model,
        taxStatus = this.taxStatus.toDomain(),
        taxExpiryDate = this.taxedUntil.toLocalDateOrNull(),
        motStatus = this.motStatus.toDomain(),
        motExpiryDate = this.motExpiryDate.toLocalDateOrNull(),
        sornStart = this.sornStart.toLocalDateOrNull(),
        currentLicencePaymentMethod = this.currentLicencePaymentMethod
    )
}
