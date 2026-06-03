package uk.gov.govuk.design.ui.model

import androidx.compose.ui.graphics.Color

data class Button(
    val text: String,
    val onClick: () -> Unit,
    val isDestructive: Boolean = false,
    val isEnabled: Boolean = true,
    val isExternal: Boolean = false
)

data class ButtonColours(
    val containerActive: Color,
    val containerInactive: Color
)
