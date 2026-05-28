package uk.gov.govuk.design.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.window.core.layout.WindowSizeClass
import uk.gov.govuk.design.ui.component.ConnectedButton.FIRST
import uk.gov.govuk.design.ui.component.ConnectedButton.SECOND
import uk.gov.govuk.design.ui.model.Button
import uk.gov.govuk.design.ui.model.ButtonColours
import uk.gov.govuk.design.ui.model.SINGLE_COLUMN_THRESHOLD_DP
import uk.gov.govuk.design.ui.theme.GovUkTheme

@Composable
fun FixedPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    externalLink: Boolean = false
) {
    Column(modifier.fillMaxWidth()) {
        FixedContainerDivider()
        MediumVerticalSpacer()
        PrimaryButton(
            text = text,
            onClick = onClick,
            modifier = Modifier.padding(horizontal = GovUkTheme.spacing.medium),
            enabled = enabled,
            externalLink = externalLink
        )
        ExtraLargeVerticalSpacer()
    }
}

@Composable
fun FixedSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    externalLink: Boolean = false
) {
    Column(modifier.fillMaxWidth()) {
        FixedContainerDivider()
        MediumVerticalSpacer()
        SecondaryButton(
            text = text,
            onClick = onClick,
            modifier = Modifier.padding(horizontal = GovUkTheme.spacing.medium),
            enabled = enabled,
            externalLink = externalLink
        )
        ExtraLargeVerticalSpacer()
    }
}

@Composable
fun FixedDoubleButtonGroup(
    primaryButton: Button,
    secondaryButton: Button,
    modifier: Modifier = Modifier,
    isWindowHeightCompact: Boolean = isWindowHeightCompact()
) {
    Column(modifier.fillMaxWidth()) {
        FixedContainerDivider()
        MediumVerticalSpacer()
        DoubleButtonGroup(
            primaryButton = primaryButton,
            secondaryButton = secondaryButton,
            isWindowHeightCompact = isWindowHeightCompact
        )
        ExtraLargeVerticalSpacer()
    }
}

@Composable
fun DoubleButtonGroup(
    primaryButton: Button,
    secondaryButton: Button,
    modifier: Modifier = Modifier,
    isWindowHeightCompact: Boolean = isWindowHeightCompact()
) {
    if (isWindowHeightCompact) {
        HorizontalButtonGroup(
            primaryButton = primaryButton,
            secondaryButton = secondaryButton,
            modifier = modifier.padding(horizontal = GovUkTheme.spacing.medium),
        )
    } else {
        VerticalButtonGroup(
            primaryButton = primaryButton,
            secondaryButton = secondaryButton,
            modifier = modifier.padding(horizontal = GovUkTheme.spacing.medium),
        )
    }
}

@Composable
private fun isWindowHeightCompact() : Boolean {
    val windowAdaptiveInfo = currentWindowAdaptiveInfo()
    return !windowAdaptiveInfo.windowSizeClass.isHeightAtLeastBreakpoint(WindowSizeClass.HEIGHT_DP_MEDIUM_LOWER_BOUND)
}

@Composable
private fun VerticalButtonGroup(
    primaryButton: Button,
    secondaryButton: Button,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        if (primaryButton.isDestructive) {
            DestructiveButton(
                text = primaryButton.text,
                onClick = primaryButton.onClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = primaryButton.isEnabled
            )
        } else {
            PrimaryButton(
                text = primaryButton.text,
                onClick = primaryButton.onClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = primaryButton.isEnabled
            )
        }
        MediumVerticalSpacer()
        SecondaryButton(
            text = secondaryButton.text,
            onClick = secondaryButton.onClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = secondaryButton.isEnabled,
            externalLink = secondaryButton.isExternal
        )
    }
}

@Composable
private fun HorizontalButtonGroup(
    primaryButton: Button,
    secondaryButton: Button,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        if (primaryButton.isDestructive) {
            DestructiveButton(
                text = primaryButton.text,
                onClick = primaryButton.onClick,
                modifier = Modifier.weight(0.5f),
                enabled = primaryButton.isEnabled
            )
        } else {
            PrimaryButton(
                text = primaryButton.text,
                onClick = primaryButton.onClick,
                modifier = Modifier.weight(0.5f),
                enabled = primaryButton.isEnabled
            )
        }
        MediumHorizontalSpacer()
        SecondaryButton(
            text = secondaryButton.text,
            onClick = secondaryButton.onClick,
            modifier = Modifier.weight(0.5f),
            enabled = secondaryButton.isEnabled
        )
    }
}

enum class ConnectedButton {
    FIRST, SECOND
}

private const val FONT_SCALE_THRESHOLD = 2.0

@Composable
fun ConnectedButtonGroup(
    firstText: String,
    secondText: String,
    onActiveStateChange: (ConnectedButton) -> Unit,
    activeButton: ConnectedButton,
    modifier: Modifier = Modifier,
    colours: ButtonColours
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val fontScale = configuration.fontScale

    if (screenWidth <= SINGLE_COLUMN_THRESHOLD_DP && fontScale >= FONT_SCALE_THRESHOLD) {
        VerticalConnectedButtonGroup(
            firstText = firstText,
            secondText = secondText,
            onActiveStateChange = onActiveStateChange,
            activeButton = activeButton,
            modifier = modifier,
            colours = colours
        )
    } else {
        HorizontalConnectedButtonGroup(
            firstText = firstText,
            secondText = secondText,
            onActiveStateChange = onActiveStateChange,
            activeButton = activeButton,
            modifier = modifier,
            colours = colours
        )
    }
}

@Composable
private fun HorizontalConnectedButtonGroup(
    firstText: String,
    secondText: String,
    onActiveStateChange: (ConnectedButton) -> Unit,
    activeButton: ConnectedButton,
    modifier: Modifier = Modifier,
    colours: ButtonColours
) {
    Row(modifier = modifier
        .fillMaxWidth()
        .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        ConnectedButton(
            text = firstText,
            onClick = {
                onActiveStateChange(FIRST)
            },
            active = activeButton == FIRST,
            modifier = Modifier
                .weight(0.5f)
                .fillMaxHeight(),
            colours = colours
        )
        SmallHorizontalSpacer()
        ConnectedButton(
            text = secondText,
            onClick = {
                onActiveStateChange(SECOND)
            },
            active = activeButton == SECOND,
            modifier = Modifier
                .weight(0.5f)
                .fillMaxHeight(),
            colours = colours
        )
    }
}

@Composable
private fun VerticalConnectedButtonGroup(
    firstText: String,
    secondText: String,
    onActiveStateChange: (ConnectedButton) -> Unit,
    activeButton: ConnectedButton,
    modifier: Modifier = Modifier,
    colours: ButtonColours
) {
    Column(modifier = modifier.fillMaxWidth()) {
        ConnectedButton(
            text = firstText,
            onClick = {
                onActiveStateChange(FIRST)
            },
            active = activeButton == FIRST,
            modifier = Modifier
                .fillMaxWidth(),
            colours = colours
        )
        SmallVerticalSpacer()
        ConnectedButton(
            text = secondText,
            onClick = {
                onActiveStateChange(SECOND)
            },
            active = activeButton == SECOND,
            modifier = Modifier
                .fillMaxWidth(),
            colours = colours
        )
    }
}

@Preview
@Composable
private fun FixedPrimaryButtonPreview()
{
    GovUkTheme {
        FixedPrimaryButton(
            text = "Primary",
            onClick = {}
        )
    }
}

@Preview
@Composable
private fun FixedSecondaryButtonPreview()
{
    GovUkTheme {
        FixedSecondaryButton(
            text = "Primary",
            onClick = {}
        )
    }
}

@Preview
@Composable
private fun VerticalButtonGroupPreview()
{
    GovUkTheme {
        FixedDoubleButtonGroup(
            primaryButton = Button("Primary", {}),
            secondaryButton = Button("Secondary", {}),
            isWindowHeightCompact = false
        )
    }
}

@Preview
@Composable
private fun HorizontalButtonGroupPreview()
{
    GovUkTheme {
        FixedDoubleButtonGroup(
            primaryButton = Button("Primary", {}),
            secondaryButton = Button("Secondary", {}),
            isWindowHeightCompact = true
        )
    }
}

@Preview
@Composable
private fun VerticalDestructiveButtonGroupPreview()
{
    val primaryButton = Button("Primary", {}, true)
    GovUkTheme {
        FixedDoubleButtonGroup(
            primaryButton = primaryButton,
            secondaryButton = Button("Secondary", {}),
            isWindowHeightCompact = false
        )
    }
}

@Preview
@Composable
private fun HorizontalDestructiveButtonGroupPreview()
{
    val primaryButton = Button("Primary", {}, true)
    GovUkTheme {
        FixedDoubleButtonGroup(
            primaryButton = primaryButton,
            secondaryButton = Button("Secondary", {}),
            isWindowHeightCompact = true
        )
    }
}

@Preview
@Composable
private fun HorizontalConnectedButtonGroupPreview()
{
    GovUkTheme {
        HorizontalConnectedButtonGroup(
            firstText = "First",
            secondText = "Second",
            onActiveStateChange = { },
            activeButton = FIRST,
            colours = ButtonColours(
                containerActive = GovUkTheme.colourScheme.surfaces.connectedButtonGroupActive,
                containerInactive = GovUkTheme.colourScheme.surfaces.connectedButtonGroupInactive
            )
        )
    }
}

@Preview
@Composable
private fun VerticalConnectedButtonGroupPreview()
{
    GovUkTheme {
        VerticalConnectedButtonGroup(
            firstText = "First",
            secondText = "Second",
            onActiveStateChange = { },
            activeButton = FIRST,
            colours = ButtonColours(
                containerActive = GovUkTheme.colourScheme.surfaces.connectedButtonGroupActive,
                containerInactive = GovUkTheme.colourScheme.surfaces.connectedButtonGroupInactive
            )
        )
    }
}
