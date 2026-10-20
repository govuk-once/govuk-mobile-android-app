package uk.gov.govuk.settings.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest

internal class YourAccountsScreenScreenshotTests {
    @PreviewTest
    @Preview(name = "YourAccountsScreenEmpty", showBackground = true)
    @Composable
    fun YourAccountsScreenEmptyScreenshots() {
        YourAccountsScreenEmptyPreview()
    }

    @PreviewTest
    @Preview(name = "YourAccountsScreen", showBackground = true)
    @Composable
    fun YourAccountsScreenScreenshots() {
        YourAccountsScreenPreview()
    }

    @PreviewTest
    @Preview(name = "YourAccountsScreenUnlinking", showBackground = true)
    @Composable
    fun YourAccountsScreenUnlinkingScreenshots() {
        YourAccountsScreenUnlinkingPreview()
    }
}