package uk.gov.govuk.dvla.domain

import uk.gov.govuk.config.data.remote.model.DvlaUrls
import uk.gov.govuk.data.extension.toLocalDateOrNull
import uk.gov.govuk.design.ui.model.AccessibleString
import uk.gov.govuk.dvla.R
import uk.gov.govuk.dvla.ui.model.MenuAction
import uk.gov.govuk.dvla.ui.model.OverflowMenuItem
import uk.gov.govuk.dvla.util.StringProvider
import java.time.LocalDate
import uk.gov.govuk.dvla.remote.model.VehicleSummary as RemoteVehicleSummary

data class VehicleSummary(
    val vehicleId: Int,
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
        vehicleId = this.vehicleId,
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

internal fun buildMenuItems(
    hasSorn: Boolean,
    isTaxed: Boolean,
    dvlaUrls: DvlaUrls?,
    stringProvider: StringProvider
): List<OverflowMenuItem> {
    dvlaUrls ?: return emptyList()
    return buildList {
        if (hasSorn) {
            add(
                OverflowMenuItem(
                    text = AccessibleString(stringProvider.getString(R.string.menu_sorn_rules)),
                    action = MenuAction.WebLink(dvlaUrls.sornRules)
                )
            )
        }
        add(
            OverflowMenuItem(
                text = AccessibleString(
                    stringProvider.getString(R.string.menu_report_as_sold),
                    stringProvider.getString(R.string.menu_report_as_sold_alt_text)
                ),
                action = MenuAction.WebLink(dvlaUrls.soldVehicle)
            )
        )
        if (!hasSorn) {
            add(
                OverflowMenuItem(
                    text = AccessibleString(
                        stringProvider.getString(R.string.menu_register_off_road),
                        stringProvider.getString(R.string.menu_register_off_road_alt_text)
                    ),
                    action = MenuAction.WebLink(dvlaUrls.makeSorn)
                )
            )
        }
        add(
            OverflowMenuItem(
                text = AccessibleString(stringProvider.getString(R.string.menu_get_log_book)),
                action = MenuAction.WebLink(dvlaUrls.getLogbook)
            )
        )
        add(
            OverflowMenuItem(
                text = AccessibleString(stringProvider.getString(R.string.menu_change_log_book_address)),
                action = MenuAction.WebLink(dvlaUrls.changeLogbookAddress)
            )
        )
        if (isTaxed) {
            add(
                OverflowMenuItem(
                    text = AccessibleString(
                        stringProvider.getString(R.string.menu_cancel_tax),
                        stringProvider.getString(R.string.menu_cancel_tax_alt_text)
                    ),
                    action = MenuAction.WebLink(dvlaUrls.cancelTax)
                )
            )
        }
    }
}
