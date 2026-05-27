package uk.gov.govuk.design.ui.model

data class Button(
    val text: String,
    val onClick: () -> Unit,
    val isDestructive: Boolean = false,
    val isEnabled: Boolean = true,
    val isExternal: Boolean = false
)
