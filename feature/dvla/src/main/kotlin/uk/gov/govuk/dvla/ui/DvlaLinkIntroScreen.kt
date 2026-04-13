package uk.gov.govuk.dvla.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import uk.gov.govuk.design.ui.component.BookendToWebScreen
import uk.gov.govuk.dvla.R

@Composable
internal fun DvlaLinkIntroScreen(
    onClose: () -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    BookendToWebScreen(
        title = stringResource(R.string.link_dvla_intro_title),
        description = stringResource(R.string.link_dvla_intro_description),
        actionMessage = stringResource(R.string.link_dvla_intro_action_message),
        buttonText = stringResource(R.string.link_dvla_intro_button),
        onClose = onClose,
        onContinue = onContinue,
        modifier = modifier
    )
}
