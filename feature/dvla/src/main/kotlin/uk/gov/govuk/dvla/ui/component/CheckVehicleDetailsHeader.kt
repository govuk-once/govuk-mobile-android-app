package uk.gov.govuk.dvla.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import uk.gov.govuk.design.R
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.dvla.ui.model.OverflowMenuItem

@Composable
internal fun CheckVehicleDetailsHeader(
    onClose: () -> Unit,
    menuItems: List<OverflowMenuItem>,
    onMenuItemClick: (OverflowMenuItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(
            vertical = 12.dp,
            horizontal = GovUkTheme.spacing.medium
        ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onClose
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_cancel),
                contentDescription = stringResource(R.string.content_desc_close),
                modifier = Modifier.size(40.dp),
                tint = GovUkTheme.colourScheme.textAndIcons.iconSecondary
            )
        }

        CardOverflowMenu(
            menuItems = menuItems,
            onMenuItemClick = onMenuItemClick
        )
    }
}

@PreviewLightDark
@Composable
private fun CheckVehicleDetailsHeaderPreview() {
    GovUkTheme {
        CheckVehicleDetailsHeader({ }, listOf(), { })
    }
}
