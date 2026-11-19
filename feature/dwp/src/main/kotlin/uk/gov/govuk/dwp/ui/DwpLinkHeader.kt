package uk.gov.govuk.dwp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import uk.gov.govuk.design.ui.component.SmallVerticalSpacer
import uk.gov.govuk.dwp.ui.component.dwpLinkCard

@Composable
fun dwpLinkHeader(
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        dwpLinkCard()
        SmallVerticalSpacer()
    }
}
