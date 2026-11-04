package uk.gov.govuk.chat.ui

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Devices.PHONE
import androidx.compose.ui.tooling.preview.Preview
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
    @Preview(
        device = "$PHONE, orientation=landscape",
        uiMode = UI_MODE_NIGHT_NO
    )
    @Composable
    fun OnboardingPageThreeLandscapeLightScreenshots() {
        OnboardingPageThreePreviewLandscapeLight()
    }

    @PreviewTest
    @Preview(
        device = "$PHONE, orientation=landscape",
        uiMode = UI_MODE_NIGHT_YES
    )
    @Composable
    fun OnboardingPageThreeLandscapeDarkScreenshots() {
        OnboardingPageThreePreviewLandscapeDark()
    }
}