package uk.gov.govuk.dvla.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.ExternalLinkListItem
import uk.gov.govuk.design.ui.component.ListHeadingLabel
import uk.gov.govuk.design.ui.component.SmallVerticalSpacer
import uk.gov.govuk.design.ui.component.Title3BoldLabel
import uk.gov.govuk.design.ui.model.ExternalLinkListItemStyle
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.dvla.R
import uk.gov.govuk.dvla.ui.model.UrlModel

@Composable
internal fun DrivingRecordSection(
    onLinkClick: (text: String, url: UrlModel) -> Unit,
    url: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Title3BoldLabel(text = stringResource(R.string.driving_record_title))

        BodyRegularLabel(
            text = stringResource(R.string.driving_record_description),
            color = GovUkTheme.colourScheme.textAndIcons.secondary
        )

        SmallVerticalSpacer()

        val linkText = stringResource(R.string.driving_record_link_text)
        ExternalLinkListItem(
            title = linkText,
            onClick = { onLinkClick(linkText, UrlModel(url)) },
            style = ExternalLinkListItemStyle.Icon
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
