package uk.gov.govuk.settings.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.android.tools.screenshot.PreviewTest

internal class YourAccountsScreenScreenshotTests {
    @PreviewTest
    @PreviewLightDark
    @Composable
    fun YourAccountsScreenEmptyScreenshots() {
        YourAccountsScreenEmptyPreview()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun YourAccountsScreenScreenshots() {
        YourAccountsScreenPreview()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun YourAccountsScreenUnlinkingScreenshots() {
        YourAccountsScreenUnlinkingPreview()
    }
}