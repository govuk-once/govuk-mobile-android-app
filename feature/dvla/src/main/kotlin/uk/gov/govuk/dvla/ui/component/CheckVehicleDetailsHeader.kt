package uk.gov.govuk.dvla.ui.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
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
    actionColour: Color = GovUkTheme.colourScheme.textAndIcons.linkSecondary
) {
    Row(
        modifier = modifier
            .height(64.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(onClick = onClose) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = stringResource(DesignR.string.content_desc_close),
                tint = actionColour
            )
        }

        Spacer(Modifier.weight(1f))

        IconButton(onClick = onSearch) {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = stringResource(R.string.search_vehicle_content_desc),
                tint = actionColour
            )
        }

        if (menuItems.isNotEmpty()) {
            OverflowMenu(
                modifier = Modifier.padding(end = 12.dp),
                menuItems = menuItems,
                onMenuItemClick = onMenuItemClick,
                overflowButton = { onClick ->
                    IconButton(onClick = onClick) {
                        Icon(
                            painter = painterResource(DesignR.drawable.ic_more),
                            contentDescription = stringResource(R.string.more_options_alt_text),
                            tint = actionColour
                        )
                    }
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