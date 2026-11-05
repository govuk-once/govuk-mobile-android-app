package uk.gov.govuk.design.ui.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import uk.gov.govuk.design.ui.theme.GovUkTheme

@Composable
fun ExtraSmallHorizontalSpacer(
    modifier: Modifier = Modifier
) {
    Spacer(modifier.width(GovUkTheme.spacing.extraSmall))
}

@Composable
fun ExtraSmallVerticalSpacer(
    modifier: Modifier = Modifier
) {
    Spacer(modifier.height(GovUkTheme.spacing.extraSmall))
}

@Composable
fun SmallHorizontalSpacer(
    modifier: Modifier = Modifier
) {
    Spacer(modifier.width(GovUkTheme.spacing.small))
}

@Composable
fun SmallVerticalSpacer(
    modifier: Modifier = Modifier
) {
    Spacer(modifier.height(GovUkTheme.spacing.small))
}

@Composable
fun MediumHorizontalSpacer(
    modifier: Modifier = Modifier
) {
    Spacer(modifier.width(GovUkTheme.spacing.medium))
}

@Composable
fun MediumVerticalSpacer(
    modifier: Modifier = Modifier
) {
    Spacer(modifier.height(GovUkTheme.spacing.medium))
}

@Composable
fun LargeHorizontalSpacer(
    modifier: Modifier = Modifier
) {
    Spacer(modifier.width(GovUkTheme.spacing.large))
}

@Composable
fun LargeVerticalSpacer(
    modifier: Modifier = Modifier
) {
    Spacer(modifier.height(GovUkTheme.spacing.large))
}

@Composable
fun ExtraLargeHorizontalSpacer(
    modifier: Modifier = Modifier
) {
    Spacer(modifier.width(GovUkTheme.spacing.extraLarge))
}

@Composable
fun ExtraLargeVerticalSpacer(
    modifier: Modifier = Modifier
) {
    Spacer(modifier.height(GovUkTheme.spacing.extraLarge))
}
