package uk.gov.govuk.design.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import uk.gov.govuk.design.R
import uk.gov.govuk.design.ui.extension.withAltText
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.design.ui.theme.ThemePreviews

@Composable
fun CentreAlignedScreen(
    modifier: Modifier = Modifier,
    screenContent: @Composable ColumnScope.() -> Unit,
    bottomContent: @Composable (ColumnScope.() -> Unit)? = null,
    footerContent: @Composable () -> Unit
) {
    Column(modifier.fillMaxSize()) {
        Column(Modifier.weight(1f)) {
            Spacer(Modifier.weight(1f))

            Column(
                Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth()
                    .padding(
                        horizontal = GovUkTheme.spacing.medium,
                        vertical = GovUkTheme.spacing.large
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                screenContent()
            }

            Spacer(Modifier.weight(1f))

            bottomContent?.let { bottomContent ->
                bottomContent()
            }
        }

        footerContent()
    }
}

@Composable
fun LoadingScreen(
    modifier: Modifier = Modifier,
    accessibilityText: String = ""
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .size(36.dp)
                .semantics {
                    contentDescription = accessibilityText
                }
            ,
            color = GovUkTheme.colourScheme.surfaces.primary
        )
    }
}

@Composable
fun BookendToWebScreen(
    title: String,
    description: String,
    actionMessage: String,
    buttonText: String,
    onClose: () -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
    descriptionAltText: String? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GovUkTheme.colourScheme.surfaces.fullScreenLinkAccount)
    ) {

        IconButton(
            onClick = onClose,
            modifier = Modifier.padding(
                top = GovUkTheme.spacing.small,
                start = GovUkTheme.spacing.extraSmall
            )
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_cancel),
                contentDescription = stringResource(R.string.content_desc_close),
                tint = GovUkTheme.colourScheme.textAndIcons.iconPrimary
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
                .padding(
                    horizontal = GovUkTheme.spacing.medium,
                    vertical = GovUkTheme.spacing.medium
                )
        ) {
            LargeTitleBoldLabel(
                text = title,
                color = GovUkTheme.colourScheme.textAndIcons.primaryInverse,
                modifier = Modifier.semantics { heading() }
            )

            MediumVerticalSpacer()

            Title3RegularLabel(
                text = description,
                color = GovUkTheme.colourScheme.textAndIcons.primaryInverse,
                modifier = modifier.withAltText(descriptionAltText)
            )

            MediumVerticalSpacer()

            Title3RegularLabel(
                text = actionMessage,
                color = GovUkTheme.colourScheme.textAndIcons.primaryInverse
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(GovUkTheme.spacing.medium)
        ) {
            AccountConnectionButton(
                text = buttonText,
                onClick = onContinue,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun BookendConnectingScreen(
    title: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GovUkTheme.colourScheme.surfaces.fullScreenLinkAccount)
            .padding(horizontal = GovUkTheme.spacing.medium),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        CircularProgressIndicator(
            modifier = Modifier
                .size(40.dp),
            color = GovUkTheme.colourScheme.textAndIcons.primaryInverse
        )

        LargeVerticalSpacer()

        LargeTitleBoldLabel(
            text = title,
            color = GovUkTheme.colourScheme.textAndIcons.primaryInverse,
            textAlign = TextAlign.Center,
            modifier = Modifier.semantics { heading() }
        )
    }
}

@Composable
fun AccountConnectionSuccessScreen(
    title: String,
    buttonText: String,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GovUkTheme.colourScheme.surfaces.fullScreenLinkAccount)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = GovUkTheme.spacing.medium),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Icon(
                painter = painterResource(id = R.drawable.ic_success_round),
                contentDescription = null,
                tint = GovUkTheme.colourScheme.textAndIcons.iconPrimary
            )

            LargeVerticalSpacer()

            LargeTitleBoldLabel(
                text = title,
                color = GovUkTheme.colourScheme.textAndIcons.primaryInverse,
                textAlign = TextAlign.Center,
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(GovUkTheme.spacing.medium)
        ) {
            AccountConnectionButton(
                text = buttonText,
                onClick = onContinue,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@ThemePreviews
@Composable
private fun BookendToWebScreenPreview() {
    GovUkTheme {
        BookendToWebScreen(
            title = "Add your driver and vehicles account",
            description = "Keep track of your applications, penalty points, and tax and MOT dates.",
            actionMessage = "We’ll take you to your web browser to sign in.",
            buttonText = "Continue",
            onClose = {},
            onContinue = {}
        )
    }
}

@ThemePreviews
@Composable
private fun BookendConnectingScreenPreview() {
    GovUkTheme {
        BookendConnectingScreen(
            title = "Add your driver and vehicles account"
        )
    }
}

@ThemePreviews
@Composable
private fun AccountConnectionSuccessScreenPreview() {
    GovUkTheme {
        AccountConnectionSuccessScreen(
            title = "Driver and vehicles account added",
            buttonText = "Continue",
            onContinue = {}
        )
    }
}