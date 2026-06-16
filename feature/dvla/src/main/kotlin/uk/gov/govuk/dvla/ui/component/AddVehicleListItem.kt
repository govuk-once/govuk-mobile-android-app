package uk.gov.govuk.dvla.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.CardListItem
import uk.gov.govuk.design.ui.component.SmallHorizontalSpacer
import uk.gov.govuk.design.ui.theme.GovUkTheme

@Composable
fun AddVehicleListItem(
    title: String,
    @DrawableRes icon: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isFirst: Boolean = true,
    isLast: Boolean = true
) {
    CardListItem(
        modifier = modifier,
        onClick = onClick,
        isFirst = isFirst,
        isLast = isLast,
        drawDivider = true
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
                color = GovUkTheme.colourScheme.textAndIcons.primary // Assuming it's blue
            )

            SmallHorizontalSpacer()

            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = GovUkTheme.colourScheme.textAndIcons.linkPrimary
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