package uk.gov.govuk.design.ui.component

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.window.core.layout.WindowSizeClass
import uk.gov.govuk.design.ui.theme.GovUkTheme

@Composable
fun OnboardingSlide(
    @StringRes title: Int,
    @StringRes body: Int,
    modifier: Modifier = Modifier,
    privacyPolicy: (@Composable () -> Unit)? = null,
    @DrawableRes image: Int? = null,
    focusRequester: FocusRequester = remember { FocusRequester() }
) {
    val windowAdaptiveInfo = currentWindowAdaptiveInfo()
    val isWindowHeightNotCompact =
        windowAdaptiveInfo.windowSizeClass.isHeightAtLeastBreakpoint(WindowSizeClass.HEIGHT_DP_MEDIUM_LOWER_BOUND)

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .fillMaxWidth()
            .padding(top = GovUkTheme.spacing.extraLarge),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isWindowHeightNotCompact) {
            image?.let { image ->
                Image(
                    painter = painterResource(id = image),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(
                            horizontal = GovUkTheme.spacing.extraLarge,
                            vertical = GovUkTheme.spacing.medium
                        )
                )
            }
        }

        LargeTitleBoldLabel(
            text = stringResource(title),
            modifier = Modifier
                .focusRequester(focusRequester)
                .focusable()
                .padding(horizontal = GovUkTheme.spacing.extraLarge)
                .semantics { heading() },
            textAlign = TextAlign.Center
        )
        MediumVerticalSpacer()
        BodyRegularLabel(
            text = stringResource(body),
            modifier = Modifier
                .focusable()
                .padding(horizontal = GovUkTheme.spacing.extraLarge),
            textAlign = TextAlign.Center
        )
        privacyPolicy?.let {
            LargeVerticalSpacer()
            it()
        }
        MediumVerticalSpacer()
    }
}
