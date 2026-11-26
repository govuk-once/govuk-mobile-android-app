package uk.gov.govuk.dwp.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.Title1BoldLabel

@Composable
internal fun HistoricalPaymentsRoute(
    benefitCode: String,
    benefitName: String,
    frequency: String,
    modifier: Modifier = Modifier
) {
    HistoricalPaymentsScreen(
        benefitCode = benefitCode,
        benefitName = benefitName,
        frequency = frequency,
        modifier = modifier
    )
}

@Composable
internal fun HistoricalPaymentsScreen(
    benefitCode: String,
    benefitName: String,
    frequency: String,
    modifier: Modifier = Modifier
) {
    Title1BoldLabel(text = "$benefitName [$benefitCode]")

    BodyRegularLabel(text = frequency)
}
