package uk.gov.govuk.dvla.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.CardListItem
import uk.gov.govuk.design.ui.component.ExtraSmallVerticalSpacer
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.component.SmallHorizontalSpacer
import uk.gov.govuk.design.ui.component.Title3BoldLabel
import uk.gov.govuk.design.ui.model.AccessibleString
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.dvla.R

private const val EXAMPLE_REG = "ABC"

private data class SearchVehicleListItemColours(
    val background: Color,
    val text: Color,
    val iconBackground: Color,
    val icon: Color
)

@Composable
private fun resolveSearchVehicleListItemColours(isFocused: Boolean): SearchVehicleListItemColours {
    return if (isFocused) {
        SearchVehicleListItemColours(
            background = GovUkTheme.colourScheme.surfaces.focused,
            text = GovUkTheme.colourScheme.textAndIcons.focused,
            iconBackground = GovUkTheme.colourScheme.surfaces.icon,
            icon = GovUkTheme.colourScheme.surfaces.focused
        )
    } else {
        SearchVehicleListItemColours(
            background = GovUkTheme.colourScheme.surfaces.list,
            text = GovUkTheme.colourScheme.textAndIcons.secondary,
            iconBackground = GovUkTheme.colourScheme.surfaces.buttonPrimary,
            icon = GovUkTheme.colourScheme.textAndIcons.buttonPrimary
        )
    }
}

@Composable
fun CheckVehicleCard(
    title: String,
    description: String,
    searchPrompt: AccessibleString,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(GovUkTheme.numbers.cornerAndroidList))
            .background(GovUkTheme.colourScheme.surfaces.cardBlue)
            .padding(GovUkTheme.spacing.medium)
    ) {
        Title3BoldLabel(
            text = title,
            color = GovUkTheme.colourScheme.textAndIcons.primary,
            modifier = Modifier.semantics { heading() }
        )

        ExtraSmallVerticalSpacer()

        BodyRegularLabel(
            text = description,
            color = GovUkTheme.colourScheme.textAndIcons.primary
        )

        MediumVerticalSpacer()

        SearchVehicleListItem(
            prompt = searchPrompt,
            onClick = onSearchClick
        )
    }
}

@Composable
private fun SearchVehicleListItem(
    prompt: AccessibleString,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val colours = resolveSearchVehicleListItemColours(isFocused = isFocused)
    val altText = prompt.altText ?: prompt.displayText

    CardListItem(
        modifier = modifier.semantics(mergeDescendants = true) {
            contentDescription = altText
            role = Role.Button
        },
        onClick = onClick,
        interactionSource = interactionSource,
        drawDivider = false,
        background = colours.background
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = GovUkTheme.spacing.medium,
                    vertical = 12.dp
                )
                .defaultMinSize(minHeight = 36.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BodyRegularLabel(
                text = prompt.displayText,
                modifier = Modifier
                    .weight(1f)
                    .clearAndSetSemantics { },
                color = colours.text
            )

            SmallHorizontalSpacer()

            RegistrationPlate(
                isFilled = false,
                registration = EXAMPLE_REG,
                modifier = Modifier.clearAndSetSemantics { } // decorative only
            )

            SmallHorizontalSpacer()

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(color = colours.iconBackground, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_search),
                    contentDescription = null,
                    tint = colours.icon,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun CheckVehicleCardPreview() {
    GovUkTheme {
        CheckVehicleCard(
            title = "Check vehicle details",
            description = "Tax and MOT information for any vehicle",
            searchPrompt = AccessibleString("Search for a vehicle"),
            onSearchClick = {}
        )
    }
}
