package uk.gov.govuk.widgets.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import uk.gov.govuk.config.data.remote.model.UserFeedbackBanner
import uk.gov.govuk.design.ui.component.UserFeedbackCard

@Composable
fun UserFeedbackBanner(
    userFeedbackBanner: UserFeedbackBanner,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    UserFeedbackCard(
        body = userFeedbackBanner.body,
        linkTitle = userFeedbackBanner.link.title,
        onClick = onClick,
        modifier = modifier
    )
}
