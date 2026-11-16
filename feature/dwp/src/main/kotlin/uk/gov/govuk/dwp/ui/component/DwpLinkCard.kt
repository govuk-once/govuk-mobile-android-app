package uk.gov.govuk.dwp.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import uk.gov.govuk.design.ui.component.AccountConnectionCard
import uk.gov.govuk.dwp.BenefitsViewModel

@Composable
internal fun dwpLinkCard(
    modifier: Modifier = Modifier
) {
    val viewModel: BenefitsViewModel = hiltViewModel()

    val title = "Add your benefits account"
    val description = "View benefits and state pension payment date, amount and history."

    AccountConnectionCard(
        title = title,
        onClick = {},
        description = description,
        descriptionAltText = "",
        modifier = modifier.fillMaxWidth()
    )
}
