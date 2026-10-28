package uk.gov.govuk.dvla.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import uk.gov.govuk.design.ui.component.InternalLinkListItem
import uk.gov.govuk.design.ui.component.TitledSection
import uk.gov.govuk.design.ui.model.AccessibleString
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.dvla.R

@Composable
internal fun DrivingCategoriesSection(
    onClick: (text: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val buttonText = stringResource(R.string.driving_categories_button_text)

    TitledSection(
        title = stringResource(R.string.driving_categories_title),
        description = stringResource(R.string.driving_categories_description),
        modifier = modifier
    ) {
        InternalLinkListItem(
            title = AccessibleString(buttonText),
            onClick = { onClick(buttonText) }
        )
    }
}

@PreviewLightDark
@Composable
private fun DrivingCategoriesSectionPreview() {
    GovUkTheme {
        DrivingCategoriesSection(onClick = {})
    }
}
