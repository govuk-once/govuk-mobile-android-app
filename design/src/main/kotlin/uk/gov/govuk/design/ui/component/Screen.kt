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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import uk.gov.govuk.design.R
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
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GovUkTheme.colourScheme.surfaces.fullScreenLinkAccount)
    ) {

        IconButton(
            onClick = onClose,
            modifier = Modifier.padding(
                top = GovUkTheme.spacing.medium,
                start = GovUkTheme.spacing.small
            )
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_cancel),
                contentDescription = "Close",
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
                color = GovUkTheme.colourScheme.textAndIcons.primaryInverse
            )

            MediumVerticalSpacer()

            Title3RegularLabel(
                text = description,
                color = GovUkTheme.colourScheme.textAndIcons.primaryInverse
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
            PrimaryButton(
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