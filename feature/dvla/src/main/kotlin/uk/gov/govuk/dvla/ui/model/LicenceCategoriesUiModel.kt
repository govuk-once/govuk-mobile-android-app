package uk.gov.govuk.dvla.ui.model

import androidx.annotation.DrawableRes

internal data class LicenceCategoriesUiModel(
    val canDrive: List<LicenceCategoryGroupUiModel>,
    val provisionallyDrive: List<LicenceCategoryGroupUiModel>
)

internal data class LicenceCategoryGroupUiModel(
    val title: String,
    val categories: List<LicenceCategoryUiModel>
)

internal data class LicenceCategoryUiModel(
    val code: String,
    @DrawableRes val icon: Int? = null
)