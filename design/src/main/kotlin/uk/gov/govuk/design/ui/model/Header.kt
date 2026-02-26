package uk.gov.govuk.design.ui.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.graphics.vector.ImageVector
import uk.gov.govuk.design.R

sealed interface HeaderActionStyle {
    data object None : HeaderActionStyle
    data class ActionButton(
        val title: String,
        val onClick: () -> Unit,
        val altText: String? = null
    ) : HeaderActionStyle
}

sealed class HeaderDismissStyle {
    data object None : HeaderDismissStyle()
    sealed class DismissButton(
        val icon: ImageVector,
        val altText: Int,
        val onClick: () -> Unit
    ) : HeaderDismissStyle()
    class Close(onClick: () -> Unit) : DismissButton(
        icon = Icons.Filled.Close,
        altText = R.string.content_desc_close,
        onClick = onClick
    )
    class Back(onClick: () -> Unit) : DismissButton(
        icon = Icons.AutoMirrored.Filled.ArrowBack,
        altText = R.string.content_desc_back,
        onClick = onClick
    )
}
