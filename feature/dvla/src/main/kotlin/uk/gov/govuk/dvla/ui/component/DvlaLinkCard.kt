package uk.gov.govuk.dvla.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import uk.gov.govuk.data.identity.model.ServiceLinkStatus
import uk.gov.govuk.design.ui.component.AccountConnectionCard
import uk.gov.govuk.design.ui.component.LoaderCard
import uk.gov.govuk.dvla.R

@Composable
internal fun DvlaLinkCard(
    state: ServiceLinkStatus,
    onActionClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    when (state) {
        ServiceLinkStatus.LINKED -> Unit  // draw nothing if linked (Summary widget will be shown)

        ServiceLinkStatus.UNLINKED -> {
            val title = stringResource(R.string.link_dvla_account_title)
            val description = stringResource(R.string.link_dvla_account_description)

            AccountConnectionCard(
                title = title,
                onClick = { onActionClick(title) },
                description = description,
                descriptionAltText = description.replace(
                    stringResource(R.string.acronym_mot),
                    stringResource(R.string.acronym_mot_alt_text)
                ),
                modifier = modifier.fillMaxWidth()
            )
        }

        ServiceLinkStatus.CHECKING -> {
            LoaderCard(
                modifier = modifier.fillMaxWidth(),
                altText = stringResource(R.string.link_dvla_loading)
            )
        }
    }
}
