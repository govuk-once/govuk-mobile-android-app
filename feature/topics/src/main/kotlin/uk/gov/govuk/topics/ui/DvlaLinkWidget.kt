package uk.gov.govuk.topics.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import uk.gov.govuk.design.ui.component.DrillInCard
import uk.gov.govuk.topics.DvlaLinkState
import uk.gov.govuk.topics.R

@Composable
internal fun DvlaLinkWidget(
    state: DvlaLinkState,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val titleResId = when (state) {
        DvlaLinkState.UNLINKED -> R.string.link_dvla_account_button
        DvlaLinkState.LINKED -> R.string.unlink_dvla_account_button
        DvlaLinkState.CHECKING -> null
    }

    // only show card when state known
    if (titleResId != null) {
        DrillInCard(
            title = stringResource(titleResId),
            onClick = onActionClick,
            modifier = modifier.fillMaxWidth()
        )
    }
}