package uk.gov.govuk.dvla.ui.model

import uk.gov.govuk.config.data.remote.model.DvlaUrls
import uk.gov.govuk.design.ui.model.AccessibleString
import uk.gov.govuk.dvla.R
import uk.gov.govuk.dvla.domain.CustomerVehicle
import uk.gov.govuk.dvla.domain.TaxStatus
import uk.gov.govuk.dvla.util.StringProvider
import javax.inject.Inject

internal class VehicleSummaryMapper @Inject constructor(
    private val stringProvider: StringProvider,
    private val taxAndMotStatusMapper: TaxAndMotStatusMapper
) {
    fun toUiModel(vehicle: CustomerVehicle, dvlaUrls: DvlaUrls?): VehicleSummaryUiModel {
        return VehicleSummaryUiModel(
            registration = vehicle.registration,
            make = vehicle.make,
            model = vehicle.model
                ?: "Unknown", // TODO return unknown for now, other states in future tickets
            taxStatus = taxAndMotStatusMapper.getTaxStatus(vehicle = vehicle, dvlaUrls = dvlaUrls),
            motStatus = taxAndMotStatusMapper.getMotStatus(vehicle = vehicle, dvlaUrls = dvlaUrls),
            menuItems = buildMenuItems(
                hasSorn = vehicle.sornStart != null,
                isTaxed = vehicle.taxStatus == TaxStatus.TAXED,
                dvlaUrls = dvlaUrls
            )
        )
    }

    private fun buildMenuItems(
        hasSorn: Boolean,
        isTaxed: Boolean,
        dvlaUrls: DvlaUrls?
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
}
