package uk.gov.govuk.design.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uk.gov.govuk.design.R
import uk.gov.govuk.design.ui.extension.drawBottomStroke
import uk.gov.govuk.design.ui.theme.GovUkTheme

data class GovUkButtonColours(
    val defaultContainerColour: Color,
    val defaultContentColour: Color,
    val defaultBorderColour: Color? = null,
    val defaultStrokeColour: Color? = null,
    val focussedContainerColour: Color,
    val focussedContentColour: Color,
    val focussedBorderColour: Color? = null,
    val focussedStrokeColour: Color? = null,
    val pressedContainerColour: Color,
    val pressedContentColour: Color,
    val pressedBorderColour: Color? = null,
    val pressedStrokeColour: Color? = null,
    val disabledContainerColour: Color,
    val disabledContentColour: Color,
    val disabledBorderColour: Color? = null
)

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    externalLink: Boolean = false
) {
    val colours = GovUkButtonColours(
        defaultContainerColour = GovUkTheme.colourScheme.surfaces.buttonPrimary,
        defaultContentColour = GovUkTheme.colourScheme.textAndIcons.buttonPrimary,
        defaultStrokeColour = GovUkTheme.colourScheme.surfaces.buttonPrimaryStroke,
        focussedContainerColour = GovUkTheme.colourScheme.surfaces.buttonPrimaryFocused,
        focussedContentColour = GovUkTheme.colourScheme.textAndIcons.buttonPrimaryFocused,
        focussedStrokeColour = GovUkTheme.colourScheme.surfaces.buttonPrimaryStrokeFocussed,
        pressedContainerColour = GovUkTheme.colourScheme.surfaces.buttonPrimaryHighlight,
        pressedContentColour = GovUkTheme.colourScheme.textAndIcons.buttonPrimaryHighlight,
        pressedStrokeColour = GovUkTheme.colourScheme.surfaces.buttonPrimaryStrokeHighlight,
        disabledContainerColour = GovUkTheme.colourScheme.surfaces.buttonPrimaryDisabled,
        disabledContentColour = GovUkTheme.colourScheme.textAndIcons.buttonPrimaryDisabled
    )

    BaseButton(
        text = text,
        onClick = onClick,
        colours = colours,
        textStyle = GovUkTheme.typography.bodyBold,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        externalLink = externalLink,
        shape = RoundedCornerShape(15.dp)
    )
}

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    externalLink: Boolean = false
) {
    val colours = GovUkButtonColours(
        defaultContainerColour = GovUkTheme.colourScheme.surfaces.buttonSecondary,
        defaultContentColour = GovUkTheme.colourScheme.textAndIcons.buttonSecondary,
        focussedContainerColour = GovUkTheme.colourScheme.surfaces.buttonSecondaryFocused,
        focussedContentColour = GovUkTheme.colourScheme.textAndIcons.buttonSecondaryFocused,
        pressedContainerColour = GovUkTheme.colourScheme.surfaces.buttonSecondaryHighlight,
        pressedContentColour = GovUkTheme.colourScheme.textAndIcons.buttonSecondaryHighlight,
        disabledContainerColour = GovUkTheme.colourScheme.surfaces.buttonSecondaryDisabled,
        disabledContentColour = GovUkTheme.colourScheme.textAndIcons.buttonSecondaryDisabled
    )

    BaseButton(
        text = text,
        onClick = onClick,
        colours = colours,
        textStyle = GovUkTheme.typography.bodyRegular,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        externalLink = externalLink,
        shape = RoundedCornerShape(15.dp)
    )
}

@Composable
fun CompactButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    externalLink: Boolean = false
) {
    val colours = GovUkButtonColours(
        defaultContainerColour = GovUkTheme.colourScheme.surfaces.buttonCompact,
        defaultContentColour = GovUkTheme.colourScheme.textAndIcons.buttonCompact,
        defaultBorderColour = GovUkTheme.colourScheme.strokes.cardBlue,
        focussedContainerColour = GovUkTheme.colourScheme.surfaces.buttonCompactFocused,
        focussedContentColour = GovUkTheme.colourScheme.textAndIcons.buttonCompactFocused,
        pressedContainerColour = GovUkTheme.colourScheme.surfaces.buttonCompactHighlight,
        pressedContentColour = GovUkTheme.colourScheme.textAndIcons.buttonCompactHighlight,
        pressedBorderColour = GovUkTheme.colourScheme.strokes.buttonCompactHighlight,
        disabledContainerColour = GovUkTheme.colourScheme.surfaces.buttonCompactDisabled,
        disabledContentColour = GovUkTheme.colourScheme.textAndIcons.buttonCompactDisabled
    )

    BaseButton(
        text = text,
        onClick = onClick,
        colours = colours,
        textStyle = GovUkTheme.typography.bodyRegular,
        modifier = modifier,
        enabled = enabled,
        externalLink = externalLink
    )
}

@Composable
fun DestructiveButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    externalLink: Boolean = false
) {
    val colours = GovUkButtonColours(
        defaultContainerColour = GovUkTheme.colourScheme.surfaces.buttonDestructive,
        defaultContentColour = GovUkTheme.colourScheme.textAndIcons.buttonPrimary,
        defaultStrokeColour = GovUkTheme.colourScheme.surfaces.buttonDestructiveStroke,
        focussedContainerColour = GovUkTheme.colourScheme.surfaces.buttonPrimaryFocused,
        focussedContentColour = GovUkTheme.colourScheme.textAndIcons.buttonPrimaryFocused,
        focussedStrokeColour = GovUkTheme.colourScheme.surfaces.buttonDestructiveStrokeFocussed,
        pressedContainerColour = GovUkTheme.colourScheme.surfaces.buttonDestructiveHighlight,
        pressedContentColour = GovUkTheme.colourScheme.textAndIcons.buttonPrimaryHighlight,
        pressedStrokeColour = GovUkTheme.colourScheme.surfaces.buttonDestructiveStrokeHighlight,
        disabledContainerColour = GovUkTheme.colourScheme.surfaces.buttonPrimaryDisabled,
        disabledContentColour = GovUkTheme.colourScheme.textAndIcons.buttonPrimaryDisabled
    )

    BaseButton(
        text = text,
        onClick = onClick,
        colours = colours,
        textStyle = GovUkTheme.typography.bodyBold,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        externalLink = externalLink,
        shape = RoundedCornerShape(15.dp)
    )
}

@Composable
fun ConnectedButton(
    text: String,
    onClick: () -> Unit,
    active: Boolean,
    modifier: Modifier = Modifier
) {
    val containerColour =
        if (active) {
            GovUkTheme.colourScheme.surfaces.connectedButtonGroupActive
        } else {
            GovUkTheme.colourScheme.surfaces.connectedButtonGroupInactive
        }

    val contentColour =
        if (active) {
            GovUkTheme.colourScheme.textAndIcons.header
        } else {
            GovUkTheme.colourScheme.textAndIcons.secondary
        }

    val altText = if (active) {
        "$text + ${stringResource(R.string.selected_alt_text)}"
    } else {
        text
    }

    Button(
        onClick = onClick,
        modifier = modifier.clearAndSetSemantics {
            role = Role.Button
            contentDescription = altText
        },
        shape = RoundedCornerShape(15.dp),
        colors = buttonColors(
            containerColor = containerColour,
            contentColor = contentColour
        )
    ) {
        Text(
            text = text,
            style = if (active) GovUkTheme.typography.bodyBold else
                GovUkTheme.typography.bodyRegular,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun BaseButton(
    text: String,
    onClick: () -> Unit,
    colours: GovUkButtonColours,
    textStyle: TextStyle,
    modifier: Modifier = Modifier,
    externalLink: Boolean = false,
    enabled: Boolean = true,
    shape: RoundedCornerShape = RoundedCornerShape(15.dp),
) {
    val altText = text.replace(
        stringResource(R.string.gov_uk),
        stringResource(R.string.gov_uk_alt_text)
    )

    val interactionSource = remember { MutableInteractionSource() }
    val focusRequester = remember { FocusRequester() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val isPressed by interactionSource.collectIsPressedAsState()
    val isHovered by interactionSource.collectIsHoveredAsState()

    var stateMappedColours = buttonColors(
        containerColor = colours.defaultContainerColour,
        contentColor = colours.defaultContentColour,
        disabledContainerColor = colours.disabledContainerColour,
        disabledContentColor = colours.disabledContentColour,
    )

    stateMappedColours = when {
        isFocused -> stateMappedColours.copy(
            containerColor = colours.focussedContainerColour,
            contentColor = colours.focussedContentColour,
        )
        isPressed || isHovered -> stateMappedColours.copy(
            containerColor = colours.pressedContainerColour,
            contentColor = colours.pressedContentColour
        )
        else -> stateMappedColours
    }

    val borderColour = if (enabled) {
        when {
            isFocused -> colours.focussedBorderColour
            isPressed || isHovered -> colours.pressedBorderColour
            else -> colours.defaultBorderColour
        }
    } else {
        colours.disabledBorderColour
    }

    val strokeColour = if (enabled) {
        when {
            isFocused -> colours.focussedStrokeColour
            isPressed || isHovered -> colours.pressedStrokeColour
            else -> colours.defaultStrokeColour
        }
    } else {
        null
    }

    Button(
        onClick = onClick,
        modifier = modifier
            .drawBottomStroke(
                colour = strokeColour,
                cornerRadius = 15.dp
            )
            .focusRequester(focusRequester)
            .focusable(interactionSource = interactionSource),
        enabled = enabled,
        shape = shape,
        colors = stateMappedColours,
        border = borderColour?.let { BorderStroke(1.dp, it) },
        interactionSource = interactionSource
    ) {
        Text(
            text = text,
            style = textStyle,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(vertical = 8.dp)
                .semantics {
                    contentDescription = altText
                }
                .weight(1f, fill = false)
        )
        if (externalLink) ExternalLinkIcon()
    }
}

@Composable
private fun ExternalLinkIcon() {
    Icon(
        painter = painterResource(id = R.drawable.ic_external_link),
        contentDescription = stringResource(R.string.opens_in_web_browser),
        modifier = Modifier
            .padding(start = GovUkTheme.spacing.small)
            .testTag("openInNewTabIcon")
    )
}

@Preview
@Composable
private fun Primary()
{
    GovUkTheme {
        PrimaryButton(
            text = "Primary button",
            onClick = { }
        )
    }
}

@Preview
@Composable
private fun PrimaryExternalLink()
{
    GovUkTheme {
        PrimaryButton(
            text = "Primary button",
            onClick = { },
            externalLink = true
        )
    }
}

@Preview
@Composable
private fun PrimaryDisabled()
{
    GovUkTheme {
        PrimaryButton(
            text = "Primary button",
            onClick = { },
            enabled = false
        )
    }
}

@Preview
@Composable
private fun Secondary()
{
    GovUkTheme {
        SecondaryButton(
            text = "Secondary button",
            onClick = { }
        )
    }
}

@Preview
@Composable
private fun SecondaryExternalLink()
{
    GovUkTheme {
        SecondaryButton(
            text = "Secondary button",
            onClick = { },
            externalLink = true
        )
    }
}

@Preview
@Composable
private fun SecondaryDisabled()
{
    GovUkTheme {
        SecondaryButton(
            text = "Secondary button",
            onClick = { },
            enabled = false
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun Compact()
{
    GovUkTheme {
        CompactButton(
            text = "Compact button",
            onClick = { }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CompactExternalLink()
{
    GovUkTheme {
        CompactButton(
            text = "Compact button",
            onClick = { },
            externalLink = true
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CompactDisabled()
{
    GovUkTheme {
        CompactButton(
            text = "Compact button",
            onClick = { },
            enabled = false
        )
    }
}

@Preview
@Composable
private fun Destructive()
{
    GovUkTheme {
        DestructiveButton(
            text = "Compact button",
            onClick = { }
        )
    }
}

@Preview
@Composable
private fun DestructiveExternalLink()
{
    GovUkTheme {
        DestructiveButton(
            text = "Compact button",
            onClick = { },
            externalLink = true
        )
    }
}

@Preview
@Composable
private fun DestructiveDisabled()
{
    GovUkTheme {
        DestructiveButton(
            text = "Compact button",
            onClick = { },
            enabled = false
        )
    }
}

@Preview
@Composable
private fun ConnectedActive()
{
    GovUkTheme {
        ConnectedButton(
            text = "Connected button",
            onClick = { },
            active = true
        )
    }
}

@Preview
@Composable
private fun ConnectedInactive()
{
    GovUkTheme {
        ConnectedButton(
            text = "Connected button",
            onClick = { },
            active = false
        )
    }
}