package uk.gov.govuk.dvla.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import uk.gov.govuk.design.R
import uk.gov.govuk.design.ui.model.AccessibleString
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.dvla.ui.model.MenuAction
import uk.gov.govuk.dvla.ui.model.OverflowMenuItem

// TODO: Improve FullScreenHeader to accept menu items and replace this custom header
@Composable
internal fun VehicleDetailsHeader(
    onBack: () -> Unit,
    menuItems: List<OverflowMenuItem>,
    onMenuItemClick: (OverflowMenuItem) -> Unit,
    modifier: Modifier = Modifier,
    backgroundColour: Color = GovUkTheme.colourScheme.surfaces.background,
    actionColour: Color = GovUkTheme.colourScheme.textAndIcons.linkSecondary,
) {
    Row(
        modifier = modifier
            .height(64.dp)
            .fillMaxWidth()
            .background(backgroundColour),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(
            onClick = onBack
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.content_desc_back),
                tint = actionColour
            )
        }

        if (menuItems.isNotEmpty()){
            Spacer(Modifier.weight(1f))

            CardOverflowMenu(
                Modifier.padding(end = GovUkTheme.spacing.medium),
                menuItems = menuItems,
                onMenuItemClick = onMenuItemClick,
                actionColour = actionColour
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun VehicleDetailsHeaderPreview() {
    GovUkTheme {
        VehicleDetailsHeader(
            {},
            listOf(
                OverflowMenuItem(
                    text = AccessibleString("Change address"),
                    action = MenuAction.WebLink("https://www.gov.uk/change-naddress")
                )
            ),
            { }
        )
    }
}