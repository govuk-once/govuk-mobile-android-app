package uk.gov.govuk.dvla.ui.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.CardListItem
import uk.gov.govuk.design.ui.component.ExternalLinkListItem
import uk.gov.govuk.design.ui.component.Title1BoldLabel
import uk.gov.govuk.design.ui.model.ExternalLinkListItemStyle
import uk.gov.govuk.design.ui.theme.GovUkTheme

@Composable
internal fun LicenceStatusCard(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    linkText: String? = null,
    onLinkClick: (text: String) -> Unit = {}
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = GovUkTheme.colourScheme.surfaces.cardDefault
        )
    ) {
        Spacer(Modifier.height(64.dp))

        CardListItem(isFirst = true, isLast = false) {
            Title1BoldLabel(
                text = title,
                modifier = Modifier
                    .padding(GovUkTheme.spacing.medium)
                    .semantics { heading() }
            )
        }

        CardListItem(isFirst = false, isLast = linkText == null) {
            BodyRegularLabel(
                text = description,
                modifier = Modifier.padding(
                    horizontal = GovUkTheme.spacing.medium,
                    vertical = GovUkTheme.spacing.large
                )
            )
        }

        linkText?.let { text ->
            ExternalLinkListItem(
                title = text,
                onClick = { onLinkClick(text) },
                isFirst = false,
                isLast = true,
                style = ExternalLinkListItemStyle.Icon
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun LicenceStatusCardPreview() {
    GovUkTheme {
        LicenceStatusCard(
            title = "No licence",
            description = "You do not hold a driving licence issued in England, Scotland or Wales",
            linkText = "Visit GOV.UK"
        )
    }
}

@PreviewLightDark
@Composable
private fun LicenceStatusCardWithoutLinkPreview() {
    GovUkTheme {
        LicenceStatusCard(
            title = "No licence",
            description = "You do not hold a driving licence issued in England, Scotland or Wales"
        )
    }
}
