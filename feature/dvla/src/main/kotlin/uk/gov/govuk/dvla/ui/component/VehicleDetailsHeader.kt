package uk.gov.govuk.dvla.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import uk.gov.govuk.design.R
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.dvla.ui.model.OverflowMenuItem

@Composable
internal fun VehicleDetailsHeader(
    onBack: () -> Unit,
    menuItems: List<OverflowMenuItem>,
    onMenuItemClick: (OverflowMenuItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(
            start = 0.dp,
            top = 12.dp,
            end = GovUkTheme.spacing.medium,
            bottom = 12.dp,
        ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(
            onClick = onBack
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.content_desc_back),
                tint = GovUkTheme.colourScheme.textAndIcons.linkSecondary
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
private fun VehicleDetailsHeaderPreview() {
    GovUkTheme {
        VehicleDetailsHeader({}, listOf(), { })
    }
}