package uk.gov.govuk.dvla.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import uk.gov.govuk.design.ui.component.AccountConnectionCard
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.dvla.DvlaLinkState
import uk.gov.govuk.dvla.R

@Composable
internal fun DvlaLinkCard(
    state: DvlaLinkState,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val titleResId = when (state) {
        DvlaLinkState.UNLINKED -> R.string.link_dvla_account_title
        DvlaLinkState.LINKED -> null
        DvlaLinkState.CHECKING -> null
    }

    if (titleResId != null) {
        AccountConnectionCard(
            title = stringResource(titleResId),
            onClick = onActionClick,
            description = stringResource(R.string.link_dvla_account_description),
            modifier =  modifier.fillMaxWidth()
        )
    } else {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = GovUkTheme.spacing.medium),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = GovUkTheme.colourScheme.surfaces.primary
            )
        }
    }
}