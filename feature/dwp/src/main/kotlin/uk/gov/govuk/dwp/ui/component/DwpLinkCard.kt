package uk.gov.govuk.dwp.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import uk.gov.govuk.data.identity.model.ServiceLinkStatus
import uk.gov.govuk.design.ui.component.AccountConnectionCard
import uk.gov.govuk.design.ui.component.LoaderCard
import uk.gov.govuk.dwp.BenefitsViewModel
import uk.gov.govuk.dwp.R

@Composable
internal fun DwpLinkCard(
    state: ServiceLinkStatus,
    onActionClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: BenefitsViewModel = hiltViewModel()

    when (state) {
        ServiceLinkStatus.UNLINKED, ServiceLinkStatus.ERROR -> {
            val title = stringResource(R.string.dwp_link_card_title)
            AccountConnectionCard(
                title = title,
                onClick = { onActionClick(title) },
                description = stringResource(R.string.dwp_link_card_description),
                modifier = modifier.fillMaxWidth()
            )
        }

        ServiceLinkStatus.CHECKING -> LoaderCard(
            modifier = modifier.fillMaxWidth(),
            altText = stringResource(R.string.dwp_link_loading)
        )

        ServiceLinkStatus.LINKED -> Unit
    }
}
