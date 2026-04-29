package uk.gov.govuk.settings.ui.model

import androidx.annotation.StringRes

data class LinkedAccountUiModel(
    @StringRes val displayTitleRes: Int,
    val onUnlink: () -> Unit
)
