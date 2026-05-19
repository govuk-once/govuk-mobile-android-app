package uk.gov.govuk.design.ui.component.error

import android.content.res.Configuration
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.delay
import uk.gov.govuk.design.R
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.CentreAlignedScreen
import uk.gov.govuk.design.ui.component.FixedPrimaryButton
import uk.gov.govuk.design.ui.component.LargeHorizontalSpacer
import uk.gov.govuk.design.ui.component.LargeTitleBoldLabel
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.theme.GovUkTheme

@Composable
fun ErrorPage(
    headerText: String,
    vararg subText: String,
    modifier: Modifier = Modifier,
    additionalContent: (@Composable () -> Unit)? = null,
    footerContent: (@Composable () -> Unit)? = null
) {
    val focusRequester = remember { FocusRequester() }

    CentreAlignedScreen(
        modifier = modifier,
        screenContent = {
            val isOrientationPortrait =
                LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT

            if (isOrientationPortrait) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_error),
                    contentDescription = null,
                    tint = GovUkTheme.colourScheme.textAndIcons.primary,
                    modifier = Modifier
                        .height(IntrinsicSize.Min)
                        .padding(all = GovUkTheme.spacing.medium)
                )
                LargeHorizontalSpacer()
            }

            LargeTitleBoldLabel(
                text = headerText,
                Modifier
                    .focusRequester(focusRequester)
                    .focusable(true)
                    .semantics { heading() },
                textAlign = TextAlign.Center
            )

            subText.forEach { text ->
                MediumVerticalSpacer()
                BodyRegularLabel(
                    text = text,
                    textAlign = TextAlign.Center
                )
            }

            additionalContent?.invoke()
        },
        footerContent = {
            footerContent?.invoke()
        }
    )
    LaunchedEffect(Unit) {
        delay(100)
        focusRequester.requestFocus()
    }
}

@Preview(showBackground = true)
@Composable
private fun ErrorPageWithoutAdditionalContentPreview() {
    GovUkTheme {
        ErrorPage(
            headerText = "Header text",
            subText = arrayOf("Sub text 1", "Sub text 2"),
            footerContent = {
                FixedPrimaryButton(
                    text = "Button text",
                    onClick = { }
                )
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ErrorPageWithAdditionalContentPreview() {
    GovUkTheme {
        ErrorPage(
            headerText = "Header text",
            subText = arrayOf("Sub text"),
            additionalContent = {
                MediumVerticalSpacer()
                BodyRegularLabel(
                    text = "Additional content",
                    textAlign = TextAlign.Center
                )
            },
            footerContent = {
                FixedPrimaryButton(
                    text = "Button text",
                    onClick = { }
                )
            }
        )
    }
}
