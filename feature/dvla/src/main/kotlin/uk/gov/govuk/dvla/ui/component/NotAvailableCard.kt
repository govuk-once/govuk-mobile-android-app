package uk.gov.govuk.dvla.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.CardListItem
import uk.gov.govuk.design.ui.component.ExternalLinkListItem
import uk.gov.govuk.design.ui.component.Title3BoldLabel
import uk.gov.govuk.design.ui.model.ExternalLinkListItemStyle
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.dvla.R

@Composable
internal fun NotAvailableCard(
    onClick: (text: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = GovUkTheme.colourScheme.surfaces.cardDefault
        )
    ) {
        CardListItem(isFirst = true, isLast = false) {
            Title3BoldLabel(
                text = stringResource(R.string.licence_not_available_title),
                modifier = Modifier.padding(GovUkTheme.spacing.medium)
            )
        }

        CardListItem(isFirst = false, isLast = false) {
            BodyRegularLabel(
                text = stringResource(R.string.licence_not_available_description),
                modifier = Modifier.padding(GovUkTheme.spacing.medium)
            )
        }

        val linkText = stringResource(R.string.licence_not_available_link_text)
        ExternalLinkListItem(
            title = linkText,
            onClick = { onClick(linkText) },
            isFirst = false,
            isLast = true,
            style = ExternalLinkListItemStyle.Icon
        )
    }
}

@PreviewLightDark
@Composable
private fun NotAvailableCardPreview() {
    GovUkTheme {
        NotAvailableCard(onClick = {})
    }
}
