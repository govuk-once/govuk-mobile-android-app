package uk.gov.govuk.notifications.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import uk.gov.govuk.design.ui.component.FixedDoubleButtonGroup
import uk.gov.govuk.design.ui.component.OnboardingSlide
import uk.gov.govuk.design.ui.component.PrivacyPolicyLink
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.notifications.R

@Composable
internal fun NotificationsScreen(
    onPageView: () -> Unit,
    @StringRes body: Int,
    onPrivacyPolicyClick: (text: String, url: String) -> Unit,
    modifier: Modifier = Modifier,
    @DrawableRes image: Int? = null,
    header: (@Composable () -> Unit)? = null,
    footer: @Composable () -> Unit
) {
    LaunchedEffect(Unit) {
        onPageView()
    }

    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        header?.invoke() ?: run {
            image ?: Spacer(Modifier)
        }

        OnboardingSlide(
            title = R.string.onboarding_screen_title,
            body = body,
            image = image,
            privacyPolicy = {
                PrivacyPolicyLink(
                    modifier = Modifier.padding(horizontal = GovUkTheme.spacing.extraLarge),
                    onClick = onPrivacyPolicyClick
                )
            },
            modifier = Modifier
                .weight(1f, fill = false)
        )
        footer()
    }
}

@Preview(showBackground = true)
@Composable
private fun NotificationsScreenPreview() {
    GovUkTheme {
        NotificationsScreen(
            {},
            body = R.string.onboarding_screen_body,
            onPrivacyPolicyClick = { _, _ -> },
            image = R.drawable.notifications_bell,
            footer = {
                FixedDoubleButtonGroup("Primary button", {}, "Secondary button", {})
            })
    }
}
