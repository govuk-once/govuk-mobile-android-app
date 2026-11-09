package uk.gov.govuk.design.ui.component.error

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import uk.gov.govuk.design.R
import uk.gov.govuk.design.ui.theme.GovUkTheme

@Composable
fun OfflineMessage(
    modifier: Modifier = Modifier,
    onButtonClick: (() -> Unit)? = null,
    focusRequester: FocusRequester = FocusRequester()
) {
    Error(
        modifier = modifier,
        title = stringResource(R.string.no_internet_title),
        description = stringResource(R.string.no_internet_description),
        buttonTitle = stringResource(R.string.try_again),
        onButtonClick = onButtonClick,
        focusRequester = focusRequester,
        icon = painterResource(id = R.drawable.ic_no_internet)
    )
}

@Preview (showBackground = true)
@Composable
private fun OfflineMessagePreview() {
    GovUkTheme {
        OfflineMessage(onButtonClick = {})
    }
}

@Preview (showBackground = true)
@Composable
private fun OfflineMessagePreviewNoTryAgain() {
    GovUkTheme {
        OfflineMessage()
    }
}
