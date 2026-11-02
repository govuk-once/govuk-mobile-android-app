package uk.gov.govuk.dvla.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import uk.gov.govuk.design.ui.component.ExternalLinkListItem
import uk.gov.govuk.design.ui.component.TitledSection
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.dvla.R
import uk.gov.govuk.dvla.ui.model.UrlModel

@Composable
internal fun DrivingRecordSection(
    onLinkClick: (text: String, url: UrlModel) -> Unit,
    url: String,
    modifier: Modifier = Modifier
) {
    val linkText = stringResource(R.string.driving_record_link_text)

    TitledSection(
        title = stringResource(R.string.driving_record_title),
        description = stringResource(R.string.driving_record_description),
        modifier = modifier
    ) {
        ExternalLinkListItem(
            title = linkText,
            onClick = { onLinkClick(linkText, UrlModel(url)) }
        )
    }
}

@PreviewLightDark
@Composable
private fun DrivingRecordSectionPreview() {
    GovUkTheme {
        DrivingRecordSection(
            onLinkClick = { _, _ -> },
            url = ""
        )
    }
}
