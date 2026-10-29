package uk.gov.govuk.dwp.ui.component

import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import uk.gov.govuk.design.ui.component.AccountConnectionCard

@Composable
internal fun DwpLinkCard(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val title = "Add your benefits account"
    val description = "View benefits and state pension payment date, amount and history."

    AccountConnectionCard(
        title = title,
        onClick = {
            Toast.makeText(
                context,
                "TODO: Fake the link-up then show the Payments screen",
                Toast.LENGTH_LONG
            ).show()
        },
        description = description,
        descriptionAltText = "",
        modifier = modifier.fillMaxWidth()
    )
}
