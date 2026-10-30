package uk.gov.govuk.design.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.android.tools.screenshot.PreviewTest

internal class ButtonScreenshotTests {
    @PreviewTest
    @PreviewLightDark
    @Composable
    fun PrimaryButtonScreenshots() {
        Primary()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun PrimaryButtonExternalLinkScreenshots() {
        PrimaryExternalLink()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun PrimaryButtonDisabledScreenshots() {
        PrimaryDisabled()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun PrimaryButtonLoadingScreenshots() {
        PrimaryLoading()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun AccountConnectionButtonScreenshots() {
        AccountConnection()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun SecondaryButtonScreenshots() {
        Secondary()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun SecondaryButtonExternalLinkScreenshots() {
        SecondaryExternalLink()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun SecondaryButtonDisabledScreenshots() {
        SecondaryDisabled()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun CompactButtonScreenshots() {
        Compact()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun CompactButtonExternalLinkScreenshots() {
        CompactExternalLink()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun CompactButtonDisabledScreenshots() {
        CompactDisabled()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun CompactPrimaryWithIconButtonScreenshots() {
        CompactPrimaryWithIcon()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun DestructiveButtonScreenshots() {
        Destructive()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun DestructiveButtonExternalLinkScreenshots() {
        DestructiveExternalLink()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun DestructiveButtonDisabledScreenshots() {
        DestructiveDisabled()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun ConnectedActiveButtonScreenshots() {
        ConnectedActive()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun ConnectedInactiveButtonScreenshots() {
        ConnectedInactive()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun OverflowButtonScreenshots() {
        Overflow()
    }
}