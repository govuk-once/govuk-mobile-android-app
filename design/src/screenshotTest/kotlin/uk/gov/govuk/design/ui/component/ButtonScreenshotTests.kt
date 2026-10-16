package uk.gov.govuk.design.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest

internal class ButtonScreenshotTests {
    @PreviewTest
    @Preview(name = "PrimaryButton", showBackground = true)
    @Composable
    fun PrimaryButtonScreenshots() {
        Primary()
    }

    @PreviewTest
    @Preview(name = "PrimaryButtonExternalLink", showBackground = true)
    @Composable
    fun PrimaryButtonExternalLinkScreenshots() {
        PrimaryExternalLink()
    }

    @PreviewTest
    @Preview(name = "PrimaryButtonDisabled", showBackground = true)
    @Composable
    fun PrimaryButtonDisabledScreenshots() {
        PrimaryDisabled()
    }

    @PreviewTest
    @Preview(name = "PrimaryButtonLoading", showBackground = true)
    @Composable
    fun PrimaryButtonLoadingScreenshots() {
        PrimaryLoading()
    }

    @PreviewTest
    @Preview(name = "AccountConnectionButton", showBackground = true)
    @Composable
    fun AccountConnectionButtonScreenshots() {
        AccountConnection()
    }

    @PreviewTest
    @Preview(name = "SecondaryButton", showBackground = true)
    @Composable
    fun SecondaryButtonScreenshots() {
        Secondary()
    }

    @PreviewTest
    @Preview(name = "SecondaryButtonExternalLink", showBackground = true)
    @Composable
    fun SecondaryButtonExternalLinkScreenshots() {
        SecondaryExternalLink()
    }

    @PreviewTest
    @Preview(name = "SecondaryButtonDisabled", showBackground = true)
    @Composable
    fun SecondaryButtonDisabledScreenshots() {
        SecondaryDisabled()
    }

    @PreviewTest
    @Preview(name = "CompactButton", showBackground = true)
    @Composable
    fun CompactButtonScreenshots() {
        Compact()
    }

    @PreviewTest
    @Preview(name = "CompactButtonExternalLink", showBackground = true)
    @Composable
    fun CompactButtonExternalLinkScreenshots() {
        CompactExternalLink()
    }

    @PreviewTest
    @Preview(name = "CompactButtonDisabled", showBackground = true)
    @Composable
    fun CompactButtonDisabledScreenshots() {
        CompactDisabled()
    }

    @PreviewTest
    @Preview(name = "CompactPrimaryWithIconButton", showBackground = true)
    @Composable
    fun CompactPrimaryWithIconButtonScreenshots() {
        CompactPrimaryWithIcon()
    }

    @PreviewTest
    @Preview(name = "DestructiveButton", showBackground = true)
    @Composable
    fun DestructiveButtonScreenshots() {
        Destructive()
    }

    @PreviewTest
    @Preview(name = "DestructiveButtonExternalLink", showBackground = true)
    @Composable
    fun DestructiveButtonExternalLinkScreenshots() {
        DestructiveExternalLink()
    }

    @PreviewTest
    @Preview(name = "DestructiveButtonDisabled", showBackground = true)
    @Composable
    fun DestructiveButtonDisabledScreenshots() {
        DestructiveDisabled()
    }

    @PreviewTest
    @Preview(name = "ConnectedActiveButton", showBackground = true)
    @Composable
    fun ConnectedActiveButtonScreenshots() {
        ConnectedActive()
    }

    @PreviewTest
    @Preview(name = "ConnectedInactiveButton", showBackground = true)
    @Composable
    fun ConnectedInactiveButtonScreenshots() {
        ConnectedInactive()
    }

    @PreviewTest
    @Preview(name = "OverflowButton", showBackground = true)
    @Composable
    fun OverflowButtonScreenshots() {
        Overflow()
    }
}