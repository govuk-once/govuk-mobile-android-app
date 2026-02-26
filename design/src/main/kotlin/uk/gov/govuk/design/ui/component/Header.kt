package uk.gov.govuk.design.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uk.gov.govuk.design.ui.model.HeaderActionStyle
import uk.gov.govuk.design.ui.model.HeaderDismissStyle
import uk.gov.govuk.design.ui.theme.GovUkTheme

@Composable
fun TabHeader(
    text: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(GovUkTheme.colourScheme.surfaces.homeHeader)
            .padding(horizontal = GovUkTheme.spacing.medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Title2BoldLabel(
            text = text,
            modifier = Modifier
                .weight(1f)
                .semantics { heading() },
            color = GovUkTheme.colourScheme.textAndIcons.header,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun FullScreenHeader(
    modifier: Modifier = Modifier,
    text: String? = null,
    dismissStyle: HeaderDismissStyle = HeaderDismissStyle.None,
    actionStyle: HeaderActionStyle = HeaderActionStyle.None
) {
    Header(
        modifier = modifier,
        text = text,
        dismissStyle = dismissStyle,
        actionStyle = actionStyle
    )
}

@Composable
fun ChildPageHeader(
    modifier: Modifier = Modifier,
    text: String? = null,
    dismissStyle: HeaderDismissStyle = HeaderDismissStyle.None,
    actionStyle: HeaderActionStyle = HeaderActionStyle.None
) {
    Header(
        modifier = modifier,
        text = text,
        backgroundColour = GovUkTheme.colourScheme.surfaces.homeHeader,
        actionColour = GovUkTheme.colourScheme.textAndIcons.linkHeader,
        titleColour = GovUkTheme.colourScheme.textAndIcons.header,
        dismissStyle = dismissStyle,
        actionStyle = actionStyle
    )
}

@Composable
fun ModalHeader(
    modifier: Modifier = Modifier,
    text: String? = null,
    dismissStyle: HeaderDismissStyle = HeaderDismissStyle.None,
    actionStyle: HeaderActionStyle = HeaderActionStyle.None
) {
    Header(
        modifier = modifier,
        text = text,
        dismissStyle = dismissStyle,
        actionStyle = actionStyle
    )
}

@Composable
private fun Header(
    modifier: Modifier = Modifier,
    text: String? = null,
    backgroundColour: Color = GovUkTheme.colourScheme.surfaces.background,
    actionColour: Color = GovUkTheme.colourScheme.textAndIcons.linkSecondary,
    titleColour: Color = GovUkTheme.colourScheme.textAndIcons.primary,
    dismissStyle: HeaderDismissStyle = HeaderDismissStyle.None,
    actionStyle: HeaderActionStyle = HeaderActionStyle.None
) {
    Column(
        modifier
            .background(backgroundColour)
            .semantics { this.hideFromAccessibility() }
    ) {
        val hasDismissButton = dismissStyle is HeaderDismissStyle.DismissButton
        val hasActionButton = actionStyle is HeaderActionStyle.ActionButton

        if (hasDismissButton || hasActionButton) {
            Row(
                modifier = Modifier
                    .height(64.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (hasDismissButton) {
                    TextButton(
                        onClick = dismissStyle.onClick
                    ) {
                        Icon(
                            imageVector = dismissStyle.icon,
                            contentDescription = stringResource(dismissStyle.altText),
                            tint = actionColour
                        )
                    }
                }
                when (actionStyle) {
                    is HeaderActionStyle.ActionButton -> {
                        Spacer(Modifier.weight(1f))

                        TextButton(
                            onClick = actionStyle.onClick
                        ) {
                            BodyRegularLabel(
                                text = actionStyle.title,
                                color = actionColour,
                                textAlign = TextAlign.End,
                                modifier = Modifier.semantics {
                                    contentDescription = actionStyle.altText ?: actionStyle.title
                                }
                            )
                        }
                    }
                    else -> { /* Do nothing */ }
                }
            }
        }

        if (text != null) {
            LargeTitleBoldLabel(
                text = text,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = GovUkTheme.spacing.medium)
                    .focusable()
                    .semantics { heading() },
                color = titleColour
            )
        }
    }
}

@Composable
fun Title(
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(GovUkTheme.colourScheme.surfaces.homeHeader)
    ) {
        LargeTitleBoldLabel(
            text = title,
            modifier = Modifier
                .padding(horizontal = GovUkTheme.spacing.medium)
                .semantics { heading() }
                .focusable(),
            color = GovUkTheme.colourScheme.textAndIcons.header
        )

        description?.let { description ->
            SmallVerticalSpacer()

            BodyRegularLabel(
                text = description,
                modifier = Modifier
                    .padding(horizontal = GovUkTheme.spacing.medium),
                color = GovUkTheme.colourScheme.textAndIcons.header
            )
        }
        SmallVerticalSpacer()
    }
}

@Preview(showBackground = true)
@Composable
private fun TabHeaderPreview() {
    GovUkTheme {
        TabHeader("Tab label")
    }
}

@Preview(showBackground = true)
@Composable
private fun FullScreenHeaderNoTextWithBackAndActionPreview() {
    GovUkTheme {
        FullScreenHeader(
            dismissStyle = HeaderDismissStyle.Back {},
            actionStyle = HeaderActionStyle.ActionButton("Done", {}, "Alt text")
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FullScreenHeaderBackAndActionPreview() {
    GovUkTheme {
        FullScreenHeader(
            text = "Child page title",
            dismissStyle = HeaderDismissStyle.Back {},
            actionStyle = HeaderActionStyle.ActionButton("Done", {}, "Alt text")
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FullScreenHeaderActionNoBackPreview() {
    GovUkTheme {
        FullScreenHeader(
            text = "Child page title",
            actionStyle = HeaderActionStyle.ActionButton("Done", {}, "Alt text")
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FullScreenHeaderBackNoActionPreview() {
    GovUkTheme {
        FullScreenHeader(
            text = "Child page title",
            dismissStyle = HeaderDismissStyle.Back {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FullScreenHeaderNoActionOrBackPreview() {
    GovUkTheme {
        FullScreenHeader(
            text = "Child page title"
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FullScreenHeaderLongTextNoActionOrBackPreview() {
    GovUkTheme {
        FullScreenHeader(
            text = "This is a very long child page title that goes on and on"
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ChildPageHeaderNoTextWithBackAndActionPreview() {
    GovUkTheme {
        ChildPageHeader(
            dismissStyle = HeaderDismissStyle.Back {},
            actionStyle = HeaderActionStyle.ActionButton("Done", {}, "Alt text")
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ChildPageHeaderBackAndActionPreview() {
    GovUkTheme {
        ChildPageHeader(
            text = "Child page title",
            dismissStyle = HeaderDismissStyle.Back {},
            actionStyle = HeaderActionStyle.ActionButton("Done", {}, "Alt text")
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ChildPageHeaderActionNoBackPreview() {
    GovUkTheme {
        ChildPageHeader(
            text = "Child page title",
            actionStyle = HeaderActionStyle.ActionButton("Done", {}, "Alt text")
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ChildPageHeaderBackNoActionPreview() {
    GovUkTheme {
        ChildPageHeader(
            text = "Child page title",
            dismissStyle = HeaderDismissStyle.Back {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ChildPageHeaderNoActionOrBackPreview() {
    GovUkTheme {
        ChildPageHeader(
            text = "Child page title"
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ChildPageHeaderLongTextNoActionOrBackPreview() {
    GovUkTheme {
        ChildPageHeader(
            text = "This is a very long child page title that goes on and on"
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ModalHeaderPreview() {
    GovUkTheme {
        ModalHeader(
            text = "Modal title",
            actionStyle = HeaderActionStyle.ActionButton(
                title = "Done",
                onClick = {}
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ModalHeaderWithClosePreview() {
    GovUkTheme {
        ModalHeader(
            text = "Modal title",
            dismissStyle = HeaderDismissStyle.Close {},
            actionStyle = HeaderActionStyle.ActionButton(
                title = "Done",
                onClick = {}
            )
        )
    }
}

@Preview
@Composable
private fun TitlePreview() {
    GovUkTheme {
        Title(
            title = "Title",
            description = "Description"
        )
    }
}
