package uk.gov.govuk.dvla.ui.model

import androidx.annotation.DrawableRes
import uk.gov.govuk.design.ui.model.AccessibleString

internal data class InfoRowUiModel(
    val title: AccessibleString,
    val subtitle: AccessibleString? = null,
    @param:DrawableRes val icon: Int
)
