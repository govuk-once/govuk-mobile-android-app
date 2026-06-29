package uk.gov.govuk.dvla.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import uk.gov.govuk.design.ui.component.StatusListItem
import uk.gov.govuk.design.ui.model.AccessibleString
import uk.gov.govuk.dvla.R
import uk.gov.govuk.dvla.ui.model.StatusRowUiModel

@Composable
internal fun ValidLicenceStatusItem(
    status: StatusRowUiModel,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        StatusListItem(
            title = status.title?.let { AccessibleString(displayText = it) },
            description = AccessibleString(
                displayText = status.description,
                altText = stringResource(R.string.licence_expiration_alt_text, status.description)
            ),
            iconStyle = status.iconStyle,
            drawDivider = false
        )

    }
}
