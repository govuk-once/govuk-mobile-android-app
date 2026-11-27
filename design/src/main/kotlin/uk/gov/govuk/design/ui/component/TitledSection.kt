package uk.gov.govuk.design.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import uk.gov.govuk.design.ui.model.AccessibleString
import uk.gov.govuk.design.ui.theme.GovUkTheme

// TODO: Current titled section, to be removed when Categories section updated to new TitledSection below
@Composable
fun TitledSectionLegacy(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Title3BoldLabel(
            text = title,
            modifier = Modifier
                .padding(top = 12.dp)
                .semantics { heading() }
        )

        SmallVerticalSpacer()

        SubheadlineRegularLabel(text = description)

        MediumVerticalSpacer()

        content()
    }
}

@Composable
fun TitledSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        BodyBoldLabel(
            text = title,
            modifier = Modifier
                .padding(top = 12.dp)
                .semantics { heading() }
        )

        MediumVerticalSpacer()

        content()
    }
}

@PreviewLightDark
@Composable
private fun TitledSectionLegacyPreview() {
    GovUkTheme {
        TitledSectionLegacy(
            title = "Title",
            description = "Description",
            modifier = Modifier.padding(GovUkTheme.spacing.medium)
        ) {
            InternalLinkListItem(
                title = AccessibleString("This is an internal list item")
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun TitledSectionPreview() {
    GovUkTheme {
        TitledSection(
            title = "Title",
            modifier = Modifier.padding(GovUkTheme.spacing.medium)
        ) {
            InternalLinkListItem(
                title = AccessibleString("This is an internal list item")
            )
        }
    }
}
