package uk.gov.govuk.design.ui.component.error

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import uk.gov.govuk.design.R
import uk.gov.govuk.design.ui.theme.GovUkTheme

@Composable
fun ProblemMessage(
    onGoToGovUkClick: () -> Unit,
    modifier: Modifier = Modifier,
    description: String? = null,
    focusRequester: FocusRequester = FocusRequester()
) {

    Error(
        title = stringResource(R.string.problem_title),
        description = description ?: stringResource(R.string.problem_description),
        buttonTitle = stringResource(R.string.go_to_the_gov_uk_website),
        onButtonClick = onGoToGovUkClick,
        modifier = modifier,
        externalLink = true,
        focusRequester = focusRequester
    )
}

@Preview
@Composable
private fun ProblemMessagePreview() {
    GovUkTheme {
        ProblemMessage({})
    }
}
