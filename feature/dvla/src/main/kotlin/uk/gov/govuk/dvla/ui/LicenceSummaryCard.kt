package uk.gov.govuk.dvla.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import uk.gov.govuk.design.ui.component.BodyBoldLabel
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.SmallVerticalSpacer
import uk.gov.govuk.design.ui.extension.drawBottomStroke
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.dvla.domain.LicenceDetails

@Composable
internal fun LicenceDetailsCard(
    details: LicenceDetails,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .drawBottomStroke(
            colour = GovUkTheme.colourScheme.strokes.cardDefault,
            cornerRadius = GovUkTheme.numbers.cornerAndroidList
        ),
        colors = CardDefaults.cardColors(
            containerColor = GovUkTheme.colourScheme.surfaces.cardDefault
        )
    ) {
        Column(
            modifier = Modifier.padding(all = GovUkTheme.spacing.medium)
        ) {
            // Licence number
            // TODO hardcoded string for demonstration purpose for this ticket, will be removed in future ticket
            BodyBoldLabel(text = "Licence number")
            BodyRegularLabel(
                text = details.licenceNumber,
                color = GovUkTheme.colourScheme.textAndIcons.secondary
            )

            SmallVerticalSpacer()

            // Type
            // TODO hardcoded string for demonstration purpose for this ticket, will be removed in future ticket
            BodyBoldLabel(text = "Type")
            BodyRegularLabel(
                text = details.type,
                color = GovUkTheme.colourScheme.textAndIcons.secondary
            )

            SmallVerticalSpacer()

            // Status
            // TODO hardcoded string for demonstration purpose for this ticket, will be removed in future ticket
            BodyBoldLabel(text = "Status")
            BodyRegularLabel(
                text = details.status,
                color = GovUkTheme.colourScheme.textAndIcons.secondary
            )

            SmallVerticalSpacer()

            // Valid from
            // TODO hardcoded string for demonstration purpose for this ticket, will be removed in future ticket
            BodyBoldLabel(text = "Valid from")
            BodyRegularLabel(
                text = details.validFrom,
                color = GovUkTheme.colourScheme.textAndIcons.secondary
            )

            SmallVerticalSpacer()

            // Valid to
            // TODO hardcoded string for demonstration purpose for this ticket, will be removed in future ticket
            BodyBoldLabel(text = "Valid to")
            BodyRegularLabel(
                text = details.validTo,
                color = GovUkTheme.colourScheme.textAndIcons.secondary
            )
        }
    }
}

@Preview
@Composable
private fun LicenceDetailsCardPreview() {
    GovUkTheme {
        LicenceDetailsCard(
            details = LicenceDetails(
                licenceNumber = "DECER607085K99AE",
                type = "Full",
                status = "Valid",
                validFrom = "2025-05-02",
                validTo = "2035-05-01"
            )
        )
    }
}