package uk.gov.govuk.design.ui.component.error

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uk.gov.govuk.design.R
import uk.gov.govuk.design.ui.component.BodyBoldLabel
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.component.SecondaryButton
import uk.gov.govuk.design.ui.component.SmallVerticalSpacer
import uk.gov.govuk.design.ui.theme.GovUkTheme

data class ErrorButtonAction(
    val buttonTitle: String,
    val onButtonClick: (() -> Unit)?
)

@Composable
fun Error(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    buttonTitle: String,
    onButtonClick: (() -> Unit)? = null,
    externalLink: Boolean = false,
    focusRequester: FocusRequester = FocusRequester(),
    icon: Painter? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(all = GovUkTheme.spacing.large),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = icon ?: painterResource(id = R.drawable.ic_error),
            contentDescription = null,
            tint = GovUkTheme.colourScheme.textAndIcons.iconTertiary,
            modifier = Modifier
                .size(32.dp)
        )

        MediumVerticalSpacer()

        BodyBoldLabel(
            text = title,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .focusRequester(focusRequester)
                .focusable()
        )

        SmallVerticalSpacer()

        BodyRegularLabel(
            text = description,
            textAlign = TextAlign.Center
        )

        onButtonClick?.let {
            MediumVerticalSpacer()

            SecondaryButton(
                text = buttonTitle,
                onClick = { onButtonClick() },
                externalLink = externalLink
            )
        }
    }
}

@Preview
@Composable
private fun MessagePreview() {
    GovUkTheme {
        Error(
            title = "Title",
            description = "Description",
            buttonTitle = "Button Title",
            onButtonClick = {}
        )
    }
}

@Preview
@Composable
private fun MessagePreviewNoButton() {
    GovUkTheme {
        Error(
            title = "Title",
            description = "Description",
            buttonTitle = "Button Title"
        )
    }
}
