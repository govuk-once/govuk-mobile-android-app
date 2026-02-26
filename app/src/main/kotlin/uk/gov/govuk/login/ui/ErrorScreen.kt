package uk.gov.govuk.login.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import uk.gov.govuk.R
import uk.gov.govuk.design.ui.component.ErrorPage
import uk.gov.govuk.design.ui.theme.GovUkTheme

@Composable
internal fun ErrorRoute(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    ErrorScreen(
        onBack = { onBack() },
        modifier = modifier
    )
}

@Composable
private fun ErrorScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {

    ErrorPage(
        headerText = stringResource(R.string.login_sign_in_error_header),
        subText = stringResource(R.string.login_sign_in_error_sub_text),
        buttonText = stringResource(R.string.login_back_and_try_again_button),
        onBack = { onBack() },
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
private fun ErrorScreenPreview() {
    GovUkTheme {
        ErrorScreen(
            onBack = { }
        )
    }
}
