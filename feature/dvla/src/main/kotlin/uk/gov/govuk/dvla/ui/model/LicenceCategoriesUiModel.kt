package uk.gov.govuk.dvla.ui.model

import androidx.annotation.DrawableRes

internal data class LicenceCategoriesUiModel(
    val canDrive: List<LicenceCategoryGroupUiModel>,
    val provisionallyDrive: List<LicenceCategoryGroupUiModel>,
    val furtherInformationLinks: List<LicenceCategoryLinkUiModel>
)

internal data class LicenceCategoryGroupUiModel(
    val title: String,
    val categories: List<LicenceCategoryUiModel>
)

internal data class LicenceCategoryUiModel(
    val code: String,
    @DrawableRes val icon: Int? = null
)

internal data class LicenceCategoryLinkUiModel(
    val text: String,
    val url: String
)

internal data class LicenceCategoryDetailUiModel(
    val title: String,
    val category: LicenceCategoryUiModel,
    val expiryIndicator: String?,
    val validFrom: String?,
    val validTo: String?,
    val restrictions: List<LicenceCategoryRestrictionUiModel>,
    val youCanDrive: String?
)

internal data class LicenceCategoryRestrictionUiModel(
    val code: String,
    val description: String
)
