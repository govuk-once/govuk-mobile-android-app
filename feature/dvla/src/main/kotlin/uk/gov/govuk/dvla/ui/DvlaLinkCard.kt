package uk.gov.govuk.dvla.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import uk.gov.govuk.design.ui.component.AccountConnectionCard
import uk.gov.govuk.design.ui.component.LoaderCard
import uk.gov.govuk.dvla.DvlaLinkState
import uk.gov.govuk.dvla.R

@Composable
internal fun DvlaLinkCard(
    state: DvlaLinkState,
    onActionClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val titleResId = when (state) {
        DvlaLinkState.UNLINKED -> R.string.link_dvla_account_title
        DvlaLinkState.LINKED -> null
        DvlaLinkState.CHECKING -> null
    }

    if (titleResId != null) {
        val title = stringResource(titleResId)

        AccountConnectionCard(
            title = stringResource(titleResId),
            onClick = { onActionClick(title) },
            description = stringResource(R.string.link_dvla_account_description),
            modifier = modifier.fillMaxWidth()
        )
    } else {
        val altText = stringResource(R.string.link_dvla_loading)
        LoaderCard(
            modifier = modifier.fillMaxWidth(),
            altText = altText
        )
    }
}
