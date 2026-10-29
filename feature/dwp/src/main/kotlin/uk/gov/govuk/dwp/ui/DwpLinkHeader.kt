package uk.gov.govuk.dwp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import uk.gov.govuk.design.ui.component.SmallVerticalSpacer
import uk.gov.govuk.dwp.ui.component.DwpLinkCard

@Composable
fun DwpLinkHeader(
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        DwpLinkCard()
        SmallVerticalSpacer()
    }
}
