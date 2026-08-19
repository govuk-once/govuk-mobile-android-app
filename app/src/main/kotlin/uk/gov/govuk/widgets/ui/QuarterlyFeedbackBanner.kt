package uk.gov.govuk.widgets.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import uk.gov.govuk.R
import uk.gov.govuk.design.ui.component.QuarterlyFeedbackCard

@Composable
fun QuarterlyFeedbackBanner(
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val body = stringResource(R.string.quarterly_feedback_body)
    val title = stringResource(R.string.quarterly_feedback_title)
    val trigger = stringResource(R.string.quarterly_feedback_trigger)

    QuarterlyFeedbackCard(
        body = body,
        title = title,
        onClick = { onClick(trigger) },
        modifier = modifier
    )
}
