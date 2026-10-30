package uk.gov.govuk.dvla.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import uk.gov.govuk.design.ui.component.LargeTitleBoldLabel
import uk.gov.govuk.design.ui.component.ListDivider
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.dvla.R
import uk.gov.govuk.dvla.ui.model.LicenceCategoryUiModel

@Composable
internal fun CategoryListItem(
    category: LicenceCategoryUiModel,
    onInfoClick: () -> Unit,
    modifier: Modifier = Modifier,
    showDivider: Boolean = true
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = GovUkTheme.spacing.medium)
    ) {
        val categoryAltText = stringResource(R.string.licence_category_at_text, category.code)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .clearAndSetSemantics {
                    contentDescription = categoryAltText
                    onClick {
                        onInfoClick()
                        true
                    }
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            LargeTitleBoldLabel(text = category.code, modifier = Modifier.weight(1f))

            category.icon?.let { icon ->
                Spacer(Modifier.width(12.dp))
                Icon(
                    painter = painterResource(icon),
                    contentDescription = null,
                    tint = GovUkTheme.colourScheme.textAndIcons.primary
                )
                Spacer(Modifier.width(12.dp))
            }

            IconButton(onClick = onInfoClick) {
                Icon(
                    imageVector = Icons.Outlined.Info, // TODO GOVUKAPP-4152 check icon
                    contentDescription = null,
                    tint = GovUkTheme.colourScheme.textAndIcons.link
                )
            }
        }

        if (showDivider) {
            ListDivider(Modifier.fillMaxWidth())
        }
    }
}

@PreviewLightDark
@Composable
private fun CategoryListItemPreview() {
    GovUkTheme {
        Column(modifier = Modifier.background(GovUkTheme.colourScheme.surfaces.background)) {
            CategoryListItem(
                category = LicenceCategoryUiModel("P", R.drawable.ic_licence_cat_p),
                onInfoClick = {}
            )
            CategoryListItem(
                category = LicenceCategoryUiModel("BE", R.drawable.ic_licence_cat_be),
                onInfoClick = {}
            )
            CategoryListItem(
                category = LicenceCategoryUiModel("f"),
                onInfoClick = {},
                showDivider = false
            )
        }
    }
}
