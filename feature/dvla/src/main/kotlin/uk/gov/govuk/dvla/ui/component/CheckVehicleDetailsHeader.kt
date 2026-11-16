package uk.gov.govuk.dvla.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import uk.gov.govuk.design.ui.component.HeaderDismissButton
import uk.gov.govuk.design.ui.component.HeaderIconButton
import uk.gov.govuk.design.ui.model.AccessibleString
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.dvla.R
import uk.gov.govuk.dvla.ui.model.MenuAction
import uk.gov.govuk.dvla.ui.model.OverflowMenuItem
import uk.gov.govuk.design.R as DesignR

@Composable
internal fun CheckVehicleDetailsHeader(
    onClose: () -> Unit,
    onSearch: () -> Unit,
    menuItems: List<OverflowMenuItem>,
    onMenuItemClick: (OverflowMenuItem) -> Unit,
    modifier: Modifier = Modifier,
    backgroundColour: Color = GovUkTheme.colourScheme.surfaces.background,
    actionColour: Color = GovUkTheme.colourScheme.textAndIcons.linkSecondary
) {
    Row(
        modifier = modifier
            .height(64.dp)
            .fillMaxWidth()
            .background(backgroundColour),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HeaderDismissButton(
            icon = Icons.Filled.Close,
            altText = stringResource(DesignR.string.content_desc_close),
            onClick = onClose,
            defaultContentColour = actionColour
        )

        Spacer(Modifier.weight(1f))

        HeaderIconButton(
            icon = Icons.Filled.Search,
            altText = stringResource(R.string.search_vehicle_content_desc),
            onClick = onSearch,
            defaultContentColour = actionColour
        )

        if (menuItems.isNotEmpty()) {
            OverflowMenu(
                modifier = Modifier.padding(end = 12.dp),
                menuItems = menuItems,
                onMenuItemClick = onMenuItemClick,
                overflowButton = { onClick ->
                    HeaderIconButton(
                        icon = ImageVector.vectorResource(DesignR.drawable.ic_more),
                        altText = stringResource(R.string.more_options_alt_text),
                        onClick = onClick,
                        defaultContentColour = actionColour
                    )
                }
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun CheckVehicleDetailsHeaderPreview() {
    GovUkTheme {
        CheckVehicleDetailsHeader(
            onClose = {},
            onSearch = {},
            menuItems = listOf(
                OverflowMenuItem(
                    text = AccessibleString("Used vehicle checks"),
                    action = MenuAction.WebLink("https://www.gov.uk/checks-when-buying-a-used-car")
                )
            ),
            onMenuItemClick = {}
        )
    }
}