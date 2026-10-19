package uk.gov.govuk.dvla.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.CardListItem
import uk.gov.govuk.design.ui.component.SmallHorizontalSpacer
import uk.gov.govuk.design.ui.theme.GovUkTheme

private data class AddVehicleListItemColours(
    val background: Color,
    val text: Color,
    val icon: Color
)

@Composable
private fun resolveAddVehicleListItemColours(isFocused: Boolean): AddVehicleListItemColours {
    return if (isFocused) {
        AddVehicleListItemColours(
            background = GovUkTheme.colourScheme.surfaces.focused,
            text = GovUkTheme.colourScheme.textAndIcons.focused,
            icon = GovUkTheme.colourScheme.textAndIcons.focused
        )
    } else {
        AddVehicleListItemColours(
            background = GovUkTheme.colourScheme.surfaces.list,
            text = GovUkTheme.colourScheme.textAndIcons.primary,
            icon = GovUkTheme.colourScheme.textAndIcons.linkPrimary
        )
    }
}
@Composable
fun AddVehicleListItem(
    title: String,
    @DrawableRes icon: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isFirst: Boolean = true,
    isLast: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val colours = resolveAddVehicleListItemColours(isFocused = isFocused)

    CardListItem(
        modifier = modifier,
        onClick = onClick,
        interactionSource = interactionSource,
        isFirst = isFirst,
        isLast = isLast,
        drawDivider = true,
        background = colours.background
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(GovUkTheme.spacing.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BodyRegularLabel(
                text = title,
                modifier = Modifier.weight(1f),
                color = colours.text
            )

            SmallHorizontalSpacer()

            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = colours.icon
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun AddVehicleListItemPreview() {
    GovUkTheme {
        AddVehicleListItem(
            title = "Add vehicle",
            icon = uk.gov.govuk.design.R.drawable.ic_add,
            onClick = {},
            isFirst = true,
            isLast = true
        )
    }
}