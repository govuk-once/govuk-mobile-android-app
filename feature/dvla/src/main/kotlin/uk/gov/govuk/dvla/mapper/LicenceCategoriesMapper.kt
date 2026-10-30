package uk.gov.govuk.dvla.mapper

import uk.gov.govuk.config.data.remote.model.DvlaUrls
import uk.gov.govuk.dvla.R
import uk.gov.govuk.dvla.domain.Entitlement
import uk.gov.govuk.dvla.domain.EntitlementType
import uk.gov.govuk.dvla.ui.model.LicenceCategoriesUiModel
import uk.gov.govuk.dvla.ui.model.LicenceCategoryGroupUiModel
import uk.gov.govuk.dvla.ui.model.LicenceCategoryLinkUiModel
import uk.gov.govuk.dvla.ui.model.LicenceCategoryUiModel
import uk.gov.govuk.dvla.util.StringProvider
import javax.inject.Inject

internal class LicenceCategoriesMapper @Inject constructor(
    private val stringProvider: StringProvider
) {
    fun toUiModel(entitlements: List<Entitlement>, dvlaUrls: DvlaUrls?) = LicenceCategoriesUiModel(
        canDrive = entitlements.toGroups(EntitlementType.FULL),
        provisionallyDrive = entitlements.toGroups(EntitlementType.PROVISIONAL),
        furtherInformationLinks = dvlaUrls?.toFurtherInformationLinks().orEmpty()
    )

    private fun List<Entitlement>.toGroups(type: EntitlementType): List<LicenceCategoryGroupUiModel> {
        // codes customer holds for this type
        val heldCodes = filter { it.categoryType == type }
            .map { it.categoryCode.uppercase() }
            .toSet()

        return allCategoryGroups
            // groups with categories the customer holds codes for
            .map { group ->
                // codes are returned as uppercase but we display some lowercase
                val heldCategories = group.categories.filter { it.code.uppercase() in heldCodes }
                group.copy(categories = heldCategories)
            }
            .filter { it.categories.isNotEmpty() }
    }

    // TODO GOVUKAPP-4152: categories not listed here are not shown, confirm with product
    // catalog of every category we display, order is the order on screen, as per GOVUKAPP-4152
    private val allCategoryGroups: List<LicenceCategoryGroupUiModel>
        get() = listOf(
            LicenceCategoryGroupUiModel(
                stringProvider.getString(R.string.categories_group_mopeds),
                listOf(
                    LicenceCategoryUiModel("P", R.drawable.ic_licence_cat_p),
                    LicenceCategoryUiModel("AM", R.drawable.ic_licence_cat_am),
                    LicenceCategoryUiModel("Q", R.drawable.ic_licence_cat_q)
                )
            ),
            LicenceCategoryGroupUiModel(
                stringProvider.getString(R.string.categories_group_motorcycles),
                listOf(LicenceCategoryUiModel("A", R.drawable.ic_licence_cat_a))
            ),
            LicenceCategoryGroupUiModel(
                stringProvider.getString(R.string.categories_group_light_vehicles),
                listOf(LicenceCategoryUiModel("B1", R.drawable.ic_licence_cat_b1))
            ),
            LicenceCategoryGroupUiModel(
                stringProvider.getString(R.string.categories_group_cars),
                listOf(
                    LicenceCategoryUiModel("B", R.drawable.ic_licence_cat_b),
                    LicenceCategoryUiModel("BE", R.drawable.ic_licence_cat_be)

                    // TODO GOVUK-4142 Check about 'B auto'
                )
            ),
            LicenceCategoryGroupUiModel(
                stringProvider.getString(R.string.categories_group_medium_vehicles),
                listOf(
                    LicenceCategoryUiModel("C1", R.drawable.ic_licence_cat_c1),
                    LicenceCategoryUiModel("C1E", R.drawable.ic_licence_cat_c1e)
                )
            ),
            LicenceCategoryGroupUiModel(
                stringProvider.getString(R.string.categories_group_minibuses),
                listOf(
                    LicenceCategoryUiModel("D1", R.drawable.ic_licence_cat_d1),
                    LicenceCategoryUiModel("D1E", R.drawable.ic_licence_cat_d1e)
                )
            ),
            LicenceCategoryGroupUiModel(
                stringProvider.getString(R.string.categories_group_other_vehicles),
                listOf(
                    LicenceCategoryUiModel("f"),
                    LicenceCategoryUiModel("k"),
                    LicenceCategoryUiModel("l"),
                    LicenceCategoryUiModel("n"),
                    LicenceCategoryUiModel("G"),
                    LicenceCategoryUiModel("H")
                )
            )
        )

    private fun DvlaUrls.toFurtherInformationLinks() = listOf(
        LicenceCategoryLinkUiModel(
            stringProvider.getString(R.string.licence_categories_link_compare),
            compareLicenceCategories
        ),
        LicenceCategoryLinkUiModel(
            stringProvider.getString(R.string.licence_categories_link_weights),
            vehicleWeightExplained
        ),
        LicenceCategoryLinkUiModel(
            stringProvider.getString(R.string.licence_categories_link_add_categories),
            addLicenceCategories
        )
    )
}