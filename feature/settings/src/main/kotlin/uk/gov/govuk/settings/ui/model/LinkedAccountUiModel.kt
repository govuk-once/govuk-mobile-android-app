package uk.gov.govuk.settings.ui.model

import androidx.annotation.StringRes

data class LinkedAccountUiModel(
    val serviceName: String,
    /** Display title should start with lowercase (e.g. 'driver and vehicle account'),
     * unless it's an acronym (e.g. 'HMRC') */
    @param:StringRes val displayTitleRes: Int
)
