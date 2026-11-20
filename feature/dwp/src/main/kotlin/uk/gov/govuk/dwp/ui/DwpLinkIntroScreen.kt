package uk.gov.govuk.dwp.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import uk.gov.govuk.design.ui.component.BookendToWebScreen
import uk.gov.govuk.dwp.R

@Composable
internal fun DwpLinkIntroScreen(
    onClose: () -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
    showError: Boolean = false
) {
    BookendToWebScreen(
        title = stringResource(R.string.dwp_intro_title),
        description = stringResource(R.string.dwp_intro_description),
        descriptionAltText = stringResource(R.string.dwp_intro_description_accessible),
        actionMessage = stringResource(if (showError) R.string.dwp_link_error else R.string.dwp_intro_action),
        buttonText = stringResource(R.string.dwp_continue),
        onClose = onClose,
        onContinue = onContinue,
        modifier = modifier
    )
}
