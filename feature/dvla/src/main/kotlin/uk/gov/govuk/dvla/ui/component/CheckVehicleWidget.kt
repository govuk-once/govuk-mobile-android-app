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
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.CardListItem
import uk.gov.govuk.design.ui.component.LargeVerticalSpacer
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.component.SmallHorizontalSpacer
import uk.gov.govuk.design.ui.component.SmallVerticalSpacer
import uk.gov.govuk.design.ui.component.SubheadlineRegularLabel
import uk.gov.govuk.design.ui.component.Title3BoldLabel
import uk.gov.govuk.design.ui.extension.withAltText
import uk.gov.govuk.design.ui.model.AccessibleString
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.dvla.CheckVehicleWidgetViewModel
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
            iconBackground = GovUkTheme.colourScheme.surfaces.searchIcon,
            icon = GovUkTheme.colourScheme.textAndIcons.iconPrimary
        )
    } else {
        SearchVehicleListItemColours(
            background = GovUkTheme.colourScheme.surfaces.list,
            text = GovUkTheme.colourScheme.textAndIcons.primary,
            iconBackground = GovUkTheme.colourScheme.surfaces.searchIcon,
            icon = GovUkTheme.colourScheme.textAndIcons.iconPrimary
        )
    }
}

@Composable
fun CheckVehicleWidget(
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: CheckVehicleWidgetViewModel = hiltViewModel()

    val searchPrompt = stringResource(R.string.check_vehicle_search_prompt)
    val description =
        stringResource(R.string.check_vehicle_description).let { desc ->
            AccessibleString(
                displayText = desc,
                altText = desc.replace(
                    oldValue = stringResource(R.string.acronym_mot),
                    newValue = stringResource(R.string.acronym_mot_alt_text)
                )
            )
        }

    Column(modifier) {
        MediumVerticalSpacer()

        Title3BoldLabel(
            text = stringResource(R.string.check_vehicle_title),
            modifier = Modifier.semantics { heading() }
        )

        SmallVerticalSpacer()

        SubheadlineRegularLabel(
            text = stringResource(R.string.check_vehicle_description),
            modifier = Modifier.withAltText(description.altText)
        )

        MediumVerticalSpacer()

        CheckVehicleCard(
            prompt = searchPrompt,
            onClick = {
                viewModel.onSearchClicked(searchPrompt)
                onSearchClick()
            }
        )

        LargeVerticalSpacer()
    }
}

@Composable
fun CheckVehicleCard(
    prompt: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val colours = resolveSearchVehicleListItemColours(isFocused = isFocused)

    CardListItem(
        modifier = modifier.semantics(mergeDescendants = true) {
            contentDescription = prompt
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
                text = prompt,
                modifier = Modifier
                    .weight(1f)
                    .clearAndSetSemantics { },
                color = colours.text
            )

            SmallHorizontalSpacer()

            RegistrationPlate(
                registration = EXAMPLE_REG,
                style = RegistrationPlateStyle.FRONT,
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

@Preview(showBackground = true)
@Composable
private fun CheckAVehicleWidgetPreview() {
    GovUkTheme {
        CheckVehicleWidget(onSearchClick = {})
    }
}

@PreviewLightDark
@Composable
private fun SearchVehicleCardPreview() {
    GovUkTheme {
        CheckVehicleCard(
            prompt = "Search for a vehicle",
            onClick = {}
        )
    }
}
