package uk.gov.govuk.dvla.ui.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    cardBackground: Color = GovUkTheme.colourScheme.surfaces.cardDefault,
    titleBackground: Color = GovUkTheme.colourScheme.surfaces.cardDefault,
    titleTextColor: Color = GovUkTheme.colourScheme.textAndIcons.primary,
    titleDrawDivider: Boolean = true,
    linkText: String? = null,
    onLinkClick: (text: String) -> Unit = {}
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = cardBackground
        )
    ) {
        CardListItem(
            isFirst = true,
            isLast = false,
            drawDivider = titleDrawDivider,
            background = titleBackground
        ) {
            Spacer(Modifier.height(64.dp))

            Title1BoldLabel(
                text = title,
                color = titleTextColor,
                modifier = Modifier
                    .padding(GovUkTheme.spacing.medium)
                    .semantics { heading() }
            )
        }

        CardListItem(isFirst = false, isLast = linkText == null) {
            BodyRegularLabel(
                text = description,
                modifier = Modifier.padding(GovUkTheme.spacing.medium)
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
