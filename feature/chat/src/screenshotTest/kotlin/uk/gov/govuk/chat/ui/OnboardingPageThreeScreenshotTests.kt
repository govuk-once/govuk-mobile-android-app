package uk.gov.govuk.chat.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.android.tools.screenshot.PreviewTest

internal class OnboardingPageThreeScreenshotTests {
    @PreviewTest
    @PreviewLightDark
    @Composable
    fun OnboardingPageThreeScreenshots() {
        OnboardingPageThreePreview()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun OnboardingPageThreeLandscapeLightScreenshots() {
        OnboardingPageThreePreviewLandscapeLight()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun OnboardingPageThreeLandscapeDarkScreenshots() {
        OnboardingPageThreePreviewLandscapeDark()
    }
}