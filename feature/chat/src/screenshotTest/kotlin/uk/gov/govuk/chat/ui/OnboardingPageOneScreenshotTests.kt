package uk.gov.govuk.chat.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest

internal class OnboardingPageOneScreenshotTests {
    @PreviewTest
    @Preview(name = "OnboardingPageOneScreen", showBackground = true)
    @Composable
    fun OnboardingPageOneScreenScreenshots() {
        OnboardingPageOnePreview()
    }
}