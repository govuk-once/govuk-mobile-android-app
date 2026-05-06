package uk.gov.govuk.settings.ui.model

import androidx.annotation.StringRes

data class LinkedAccountUiModel(
    val serviceName: String,
    @param:StringRes val displayTitleRes: Int
)
