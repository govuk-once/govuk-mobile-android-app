package uk.gov.govuk.chat.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest

internal class OnboardingPageThreeScreenshotTests {
    @PreviewTest
    @Preview(name = "OnboardingPageThree", showBackground = true)
    @Composable
    fun OnboardingPageThreeScreenshots() {
        OnboardingPageThreePreview()
    }

    @PreviewTest
    @Preview(name = "OnboardingPageThreeLandscapeLight", showBackground = true)
    @Composable
    fun OnboardingPageThreeLandscapeLightScreenshots() {
        OnboardingPageThreePreviewLandscapeLight()
    }

    @PreviewTest
    @Preview(name = "OnboardingPageThreeLandscapeDark", showBackground = true)
    @Composable
    fun OnboardingPageThreeLandscapeDarkScreenshots() {
        OnboardingPageThreePreviewLandscapeDark()
    }
}