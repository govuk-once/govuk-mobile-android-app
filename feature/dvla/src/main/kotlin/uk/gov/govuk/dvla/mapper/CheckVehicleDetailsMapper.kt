package uk.gov.govuk.dvla.mapper

import uk.gov.govuk.config.data.remote.model.DvlaUrls
import uk.gov.govuk.design.ui.model.AccessibleString
import uk.gov.govuk.dvla.R
import uk.gov.govuk.dvla.domain.MotStatus
import uk.gov.govuk.dvla.domain.TaxStatus
import uk.gov.govuk.dvla.domain.VehicleEnquiryDetails
import uk.gov.govuk.dvla.domain.VehicleSummary
import uk.gov.govuk.dvla.domain.toVehicleDetails
import uk.gov.govuk.dvla.ui.model.MenuAction
import uk.gov.govuk.dvla.ui.model.OverflowMenuItem
import uk.gov.govuk.dvla.ui.model.VehicleDetailsUiModel
import uk.gov.govuk.dvla.util.StringProvider
import javax.inject.Inject

internal class CheckVehicleDetailsMapper @Inject constructor(
    private val stringProvider: StringProvider,
    private val taxAndMotStatusMapper: TaxAndMotStatusMapper,
    private val specificationsMapper: SpecificationsMapper
) {
    fun toUiModel(vehicle: VehicleEnquiryDetails, dvlaUrls: DvlaUrls?): VehicleDetailsUiModel {
        val vehicle = vehicle.toVehicleDetails()
        return VehicleDetailsUiModel(
            make = vehicle.summary.make,
            model = "",
            registration = vehicle.summary.registration,
            keeper = null,
            specificationsIcons = listOf(
                specificationsMapper.getCalendarSpecification(vehicle.dateOfFirstRegistration),
                specificationsMapper.getFuelTypeSpecification(vehicle.fuelType),
                specificationsMapper.getColourSpecification(vehicle.colour)
            ),
            taxStatus = taxAndMotStatusMapper.getTaxStatusEnquiry(vehicle.summary, dvlaUrls),
            motStatus = taxAndMotStatusMapper.getMotStatus(vehicle.summary, dvlaUrls),
            specifications = specificationsMapper.getVehicleSpecifications(vehicle),
            menuItems = buildMenuItems(vehicle.summary, dvlaUrls)
        )
    }

    private fun buildMenuItems(
        vehicleSummary: VehicleSummary,
        dvlaUrls: DvlaUrls?
    ): List<OverflowMenuItem> {
        dvlaUrls ?: return emptyList()
        return buildList {
            add(
                OverflowMenuItem(
                    text = AccessibleString(stringProvider.getString(R.string.menu_register_to_you)),
                    action = MenuAction.WebLink(dvlaUrls.soldVehicle)
                )
            )
            add(
                OverflowMenuItem(
                    text = AccessibleString(
                        stringProvider.getString(R.string.menu_used_vehicle_checks)
                    ),
                    action = MenuAction.WebLink(dvlaUrls.buyingUsedCarChecks)
                )
            )
            add(
                OverflowMenuItem(
                    text = AccessibleString(stringProvider.getString(R.string.menu_report_as_abandoned)),
                    action = MenuAction.WebLink(dvlaUrls.reportAbandonedVehicle)
                )
            )
            when (vehicleSummary.motStatus) {
                MotStatus.VALID -> {
                    when (vehicleSummary.taxStatus) {
                        TaxStatus.UNTAXED, TaxStatus.SORN, TaxStatus.NOT_TAXED_FOR_ON_ROAD_USE -> {
                            add(
                                OverflowMenuItem(
                                    text = AccessibleString(stringProvider.getString(R.string.menu_report_an_untaxed_vehicle)),
                                    action = MenuAction.WebLink(dvlaUrls.reportUntaxedVehicle)
                                )
                            )
                        }
                        else -> { /* Do nothing */ }
                    }
                }

                MotStatus.EXPIRED -> {
                    when (vehicleSummary.taxStatus) {
                        TaxStatus.TAXED, TaxStatus.UNTAXED, TaxStatus.SORN, TaxStatus.NOT_TAXED_FOR_ON_ROAD_USE -> {
                            add(
                                OverflowMenuItem(
                                    text = AccessibleString(stringProvider.getString(R.string.menu_report_a_vehicle_with_no_mot)),
                                    action = MenuAction.WebLink(dvlaUrls.reportNoMot)
                                )
                            )
                        }
                        else -> { /* Do nothing */ }
                    }
                }
                else -> { /* Do nothing */ }
            }
        }
    }
}
