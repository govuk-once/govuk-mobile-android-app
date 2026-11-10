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
        val vehicleDetails = vehicle.toVehicleDetails()
        return VehicleDetailsUiModel(
            make = vehicleDetails.summary.make,
            model = null,
            registration = vehicleDetails.summary.registration,
            keeper = null,
            specificationsIcons = listOf(
                specificationsMapper.getCalendarSpecification(vehicleDetails.dateOfFirstRegistration),
                specificationsMapper.getFuelTypeSpecification(vehicleDetails.fuelType),
                specificationsMapper.getColourSpecification(vehicleDetails.colour)
            ),
            taxStatus = taxAndMotStatusMapper.getTaxStatusEnquiry(vehicleDetails.summary, dvlaUrls),
            motStatus = taxAndMotStatusMapper.getMotStatusEnquiry(vehicleDetails.summary, dvlaUrls),
            specifications = with(specificationsMapper) {
                listOf(
                    createMakeItem(vehicleDetails),
                    createFirstRegisteredItem(vehicleDetails),
                    createFuelTypeItem(vehicleDetails),
                    createColourItem(vehicleDetails),
                    createEngineSizeItem(vehicleDetails),
                    createEmissionsItem(vehicleDetails)
                )
            },
            menuItems = buildMenuItems(vehicleDetails.summary, dvlaUrls)
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
                    text = AccessibleString(
                        stringProvider.getString(R.string.menu_register_to_you),
                        stringProvider.getString(R.string.menu_register_to_you_alt_text)
                    ),
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
                    text = AccessibleString(
                        stringProvider.getString(R.string.menu_report_as_abandoned),
                        stringProvider.getString(R.string.menu_report_as_abandoned_alt_text)
                    ),
                    action = MenuAction.WebLink(dvlaUrls.reportAbandonedVehicle)
                )
            )
            getReportOnTheRoadUrl(vehicleSummary, dvlaUrls)?.let { url ->
                add(
                    OverflowMenuItem(
                        text = AccessibleString(
                            stringProvider.getString(R.string.menu_report_as_on_the_road),
                            stringProvider.getString(R.string.menu_report_as_on_the_road_alt_text)
                        ),
                        action = MenuAction.WebLink(url)
                    )
                )
            }
        }
    }

    /**
     * Rules:
     * If taxStatus is ‘Untaxed’, ‘SORN’, or ‘Not taxed for on road use’, and motStatus is ‘Valid’, link to Report an untaxed vehicle
     * If taxStatus is ‘Taxed’ and motStatus is ‘Not valid’, link to Report a vehicle with no MOT
     * If taxStatus is ‘Untaxed’, ‘SORN’, or ‘Not taxed for on road use’, and motStatus is ‘Not valid’, link to Report a vehicle with no MOT (this seems like the more serious crime, as the vehicle might be unsafe)
     * In all other scenarios, don’t include this button at all
     */
    private fun getReportOnTheRoadUrl(vehicle: VehicleSummary, dvlaUrls: DvlaUrls): String? {
        val isTaxed = vehicle.taxStatus == TaxStatus.TAXED
        val isNotTaxed = vehicle.taxStatus in setOf(
            TaxStatus.UNTAXED,
            TaxStatus.SORN,
            TaxStatus.NOT_TAXED_FOR_ON_ROAD_USE
        )

        return when {
            vehicle.motStatus == MotStatus.VALID && isNotTaxed -> dvlaUrls.reportUntaxedVehicle      // rule 1
            vehicle.motStatus == MotStatus.EXPIRED && (isTaxed || isNotTaxed) -> dvlaUrls.reportNoMot // rules 2 & 3
            else -> null                                                                              // rule 4
        }
    }
}
