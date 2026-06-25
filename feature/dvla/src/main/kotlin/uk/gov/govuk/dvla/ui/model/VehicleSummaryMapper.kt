package uk.gov.govuk.dvla.ui.model

import uk.gov.govuk.config.data.remote.model.DvlaUrls
import uk.gov.govuk.design.ui.model.AccessibleString
import uk.gov.govuk.design.ui.model.StatusListItemIconStyle
import uk.gov.govuk.dvla.R
import uk.gov.govuk.dvla.domain.CustomerVehicle
import uk.gov.govuk.dvla.domain.MotStatus
import uk.gov.govuk.dvla.domain.TaxStatus
import uk.gov.govuk.dvla.util.StringProvider
import uk.gov.govuk.dvla.util.resolveSummaryDescription
import uk.gov.govuk.dvla.util.toSummaryDisplayFormat
import javax.inject.Inject

internal class VehicleSummaryMapper @Inject constructor(
    private val stringProvider: StringProvider
) {
    fun toUiModel(vehicle: CustomerVehicle, dvlaUrls: DvlaUrls?): VehicleSummaryUiModel {
        val taxDate = vehicle.taxExpiryDate?.toSummaryDisplayFormat()
        val motDate = vehicle.motExpiryDate?.toSummaryDisplayFormat()

        val (taxStringResId, taxIconStyle) = getTaxStatusResources(vehicle.taxStatus)
        val (motStringResId, motIconStyle) = getMotStatusResources(vehicle.motStatus)

        return VehicleSummaryUiModel(
            registration = vehicle.registration,
            make = vehicle.make,
            model = vehicle.model ?: "Unknown", // TODO return unknown for now, other states in future tickets
            taxStatus = StatusRowUiModel(
                title = stringProvider.getString(R.string.tax_status_title),
                description = stringProvider.resolveSummaryDescription(taxStringResId, taxDate),
                iconStyle = taxIconStyle
            ),
            motStatus = StatusRowUiModel(
                title = stringProvider.getString(R.string.acronym_mot),
                titleAltText = stringProvider.getString(R.string.acronym_mot_alt_text),
                description = stringProvider.resolveSummaryDescription(motStringResId, motDate),
                iconStyle = motIconStyle
            ),
            menuItems = buildMenuItems(hasSorn = vehicle.sornStart != null, dvlaUrls = dvlaUrls)
        )
    }

    private fun buildMenuItems(hasSorn: Boolean, dvlaUrls: DvlaUrls?): List<OverflowMenuItem> {
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

    // TODO what if date is null?
    private fun getTaxStatusResources(status: TaxStatus): Pair<Int?, StatusListItemIconStyle?> =
        when (status) {
            TaxStatus.TAXED -> Pair(R.string.valid_until, StatusListItemIconStyle.Success)
            else -> Pair(null, null)
        }

    // TODO what if date is null?
    private fun getMotStatusResources(status: MotStatus): Pair<Int?, StatusListItemIconStyle?> =
        when (status) {
            MotStatus.VALID -> Pair(R.string.valid_until, StatusListItemIconStyle.Success)
            else -> Pair(null, null)
        }
}

