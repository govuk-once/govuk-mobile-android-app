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
    primaryText: String,
    onPrimary: () -> Unit,
    secondaryText: String,
    onSecondary: () -> Unit,
    modifier: Modifier = Modifier,
    primaryDestructive: Boolean = false,
    primaryEnabled: Boolean = true,
    secondaryEnabled: Boolean = true,
    isWindowHeightCompact: Boolean = isWindowHeightCompact()
) {
    Column(modifier.fillMaxWidth()) {
        FixedContainerDivider()
        MediumVerticalSpacer()
        DoubleButtonGroup(
            primaryText = primaryText,
            onPrimary = onPrimary,
            secondaryText = secondaryText,
            onSecondary = onSecondary,
            primaryDestructive = primaryDestructive,
            primaryEnabled = primaryEnabled,
            secondaryEnabled = secondaryEnabled,
            isWindowHeightCompact = isWindowHeightCompact
        )
        ExtraLargeVerticalSpacer()
    }
}

@Composable
fun DoubleButtonGroup(
    primaryText: String,
    onPrimary: () -> Unit,
    secondaryText: String,
    onSecondary: () -> Unit,
    modifier: Modifier = Modifier,
    primaryDestructive: Boolean = false,
    primaryEnabled: Boolean = true,
    secondaryEnabled: Boolean = true,
    isWindowHeightCompact: Boolean = isWindowHeightCompact()
) {
    if (isWindowHeightCompact) {
        HorizontalButtonGroup(
            primaryText = primaryText,
            onPrimary = onPrimary,
            secondaryText = secondaryText,
            onSecondary = onSecondary,
            modifier = modifier.padding(horizontal = GovUkTheme.spacing.medium),
            primaryDestructive = primaryDestructive,
            primaryEnabled = primaryEnabled,
            secondaryEnabled = secondaryEnabled
        )
    } else {
        VerticalButtonGroup(
            primaryText = primaryText,
            onPrimary = onPrimary,
            secondaryText = secondaryText,
            onSecondary = onSecondary,
            modifier = modifier.padding(horizontal = GovUkTheme.spacing.medium),
            primaryDestructive = primaryDestructive,
            primaryEnabled = primaryEnabled,
            secondaryEnabled = secondaryEnabled
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
    primaryText: String,
    onPrimary: () -> Unit,
    secondaryText: String,
    onSecondary: () -> Unit,
    modifier: Modifier = Modifier,
    primaryDestructive: Boolean = false,
    primaryEnabled: Boolean = true,
    secondaryEnabled: Boolean = true,
) {
    Column(modifier) {
        if (primaryDestructive) {
            DestructiveButton(
                text = primaryText,
                onClick = onPrimary,
                modifier = Modifier.fillMaxWidth(),
                enabled = primaryEnabled
            )
        } else {
            PrimaryButton(
                text = primaryText,
                onClick = onPrimary,
                modifier = Modifier.fillMaxWidth(),
                enabled = primaryEnabled
            )
        }
        MediumVerticalSpacer()
        SecondaryButton(
            text = secondaryText,
            onClick = onSecondary,
            modifier = Modifier.fillMaxWidth(),
            enabled = secondaryEnabled
        )
    }
}

@Composable
private fun HorizontalButtonGroup(
    primaryText: String,
    onPrimary: () -> Unit,
    secondaryText: String,
    onSecondary: () -> Unit,
    modifier: Modifier = Modifier,
    primaryDestructive: Boolean = false,
    primaryEnabled: Boolean = true,
    secondaryEnabled: Boolean = true,
) {
    Row(modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        if (primaryDestructive) {
            DestructiveButton(
                text = primaryText,
                onClick = onPrimary,
                modifier = Modifier.weight(0.5f),
                enabled = primaryEnabled
            )
        } else {
            PrimaryButton(
                text = primaryText,
                onClick = onPrimary,
                modifier = Modifier.weight(0.5f),
                enabled = primaryEnabled
            )
        }
        MediumHorizontalSpacer()
        SecondaryButton(
            text = secondaryText,
            onClick = onSecondary,
            modifier = Modifier.weight(0.5f),
            enabled = secondaryEnabled
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
    modifier: Modifier = Modifier
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
            modifier = modifier
        )
    } else {
        HorizontalConnectedButtonGroup(
            firstText = firstText,
            secondText = secondText,
            onActiveStateChange = onActiveStateChange,
            activeButton = activeButton,
            modifier = modifier
        )
    }
}

@Composable
private fun HorizontalConnectedButtonGroup(
    firstText: String,
    secondText: String,
    onActiveStateChange: (ConnectedButton) -> Unit,
    activeButton: ConnectedButton,
    modifier: Modifier = Modifier
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
                .fillMaxHeight()
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
                .fillMaxHeight()
        )
    }
}

@Composable
private fun VerticalConnectedButtonGroup(
    firstText: String,
    secondText: String,
    onActiveStateChange: (ConnectedButton) -> Unit,
    activeButton: ConnectedButton,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        ConnectedButton(
            text = firstText,
            onClick = {
                onActiveStateChange(FIRST)
            },
            active = activeButton == FIRST,
            modifier = Modifier
                .fillMaxWidth()
        )
        SmallVerticalSpacer()
        ConnectedButton(
            text = secondText,
            onClick = {
                onActiveStateChange(SECOND)
            },
            active = activeButton == SECOND,
            modifier = Modifier
                .fillMaxWidth()
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
            primaryText = "Primary",
            onPrimary = {},
            secondaryText = "Secondary",
            onSecondary = {},
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
            primaryText = "Primary",
            onPrimary = {},
            secondaryText = "Secondary",
            onSecondary = {},
            isWindowHeightCompact = true
        )
    }
}

@Preview
@Composable
private fun VerticalDestructiveButtonGroupPreview()
{
    GovUkTheme {
        FixedDoubleButtonGroup(
            primaryText = "Primary",
            onPrimary = {},
            secondaryText = "Secondary",
            onSecondary = {},
            primaryDestructive = true,
            isWindowHeightCompact = false
        )
    }
}

@Preview
@Composable
private fun HorizontalDestructiveButtonGroupPreview()
{
    GovUkTheme {
        FixedDoubleButtonGroup(
            primaryText = "Primary",
            onPrimary = {},
            secondaryText = "Secondary",
            onSecondary = {},
            primaryDestructive = true,
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
            activeButton = FIRST
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
            activeButton = FIRST
        )
    }
}