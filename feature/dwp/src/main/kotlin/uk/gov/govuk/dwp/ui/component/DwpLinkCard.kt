package uk.gov.govuk.dwp.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import uk.gov.govuk.design.ui.component.AccountConnectionCard

@Composable
internal fun DwpLinkCard(
    modifier: Modifier = Modifier
) {
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