package uk.gov.govuk.travelalerts.ui.countrylist

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.android.tools.screenshot.PreviewTest

internal class CountryListScreenScreenshotTests {
    @PreviewTest
    @PreviewLightDark
    @Composable
    fun CountryListLoadingScreenshots() {
        CountryListLoadingPreview()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun CountryListErrorScreenshots() {
        CountryListErrorPreview()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun CountryListLoadedScreenshots() {
        CountryListLoadedPreview()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun CountryListLoadedEmptyScreenshots() {
        CountryListLoadedEmptyPreview()
    }
}