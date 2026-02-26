package uk.gov.govuk.settings.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import uk.gov.govuk.design.ui.component.ErrorPage
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.settings.R
import uk.gov.govuk.settings.SignOutViewModel

@Composable
internal fun SignOutErrorRoute(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: SignOutViewModel = hiltViewModel()

    SignOutErrorScreen(
        onPageView = { viewModel.onErrorPageView() },
        onBackClick = { text ->
            viewModel.onBack(text)
            onBack() },
        modifier = modifier
    )
}

@Composable
private fun SignOutErrorScreen(
    onPageView: () -> Unit,
    onBackClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        onPageView()
    }

    ErrorPage(
        headerText = stringResource(R.string.sign_out_error_header),
        subText = stringResource(R.string.sign_out_error_sub_text),
        additionalText = stringResource(R.string.sign_out_error_additional_text),
        buttonText = stringResource(R.string.sign_out_error_go_back_to_settings_button),
        onBack = onBackClick,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
private fun SignOutErrorScreenPreview() {
    GovUkTheme {
        SignOutErrorScreen(
            onPageView = { },
            onBackClick = { }
        )
    }
}
