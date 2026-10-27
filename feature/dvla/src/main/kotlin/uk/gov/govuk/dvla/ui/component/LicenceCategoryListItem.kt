package uk.gov.govuk.dvla.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import uk.gov.govuk.design.ui.component.BodyBoldLabel
import uk.gov.govuk.design.ui.component.CardListItem
import uk.gov.govuk.design.ui.component.LargeTitleBoldLabel
import uk.gov.govuk.design.ui.component.SmallHorizontalSpacer
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.dvla.R
import uk.gov.govuk.dvla.ui.model.LicenceCategoryUiModel

@Composable
internal fun CategoryListItem(
    category: LicenceCategoryUiModel,
    onInfoClick: () -> Unit,
    modifier: Modifier = Modifier,
    isFirst: Boolean = false,
    isLast: Boolean = false
) {
    CardListItem(
        modifier = modifier.fillMaxWidth(),
        isFirst = isFirst,
        isLast = isLast
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = GovUkTheme.spacing.medium,
                    end = GovUkTheme.spacing.medium,

                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LargeTitleBoldLabel(
                text = category.code,
                modifier = Modifier.weight(1f)
            )

            category.icon?.let { icon ->
                Icon(
                    painter = painterResource(icon),
                    contentDescription = null,
                    tint = GovUkTheme.colourScheme.textAndIcons.primary // vectors are hardcoded black
                )
                SmallHorizontalSpacer()
            }

            IconButton(onClick = onInfoClick) {
                Icon(
                    imageVector = Icons.Outlined.Info, // placeholder until the design's "i" drawable is added
                    contentDescription = null,
                    tint = GovUkTheme.colourScheme.textAndIcons.primary
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun CategoryListItemPreview() {
    GovUkTheme {
        Column(modifier = Modifier.padding(GovUkTheme.spacing.medium)) {
            CategoryListItem(
                category = LicenceCategoryUiModel("P", R.drawable.ic_licence_cat_p),
                onInfoClick = {},
                isFirst = true
            )
            CategoryListItem(
                category = LicenceCategoryUiModel("BE", R.drawable.ic_licence_cat_be),
                onInfoClick = {}
            )
            CategoryListItem(
                category = LicenceCategoryUiModel("f"),
                onInfoClick = {},
                isLast = true
            )
        }
    }
}