package uk.gov.govuk.travelalerts.ui.countrylist

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest

internal class CountryListScreenScreenshotTests {
    @PreviewTest
    @Preview(name = "CountryListLoading", showBackground = true)
    @Composable
    fun CountryListLoadingScreenshots() {
        CountryListLoadingPreview()
    }

    @PreviewTest
    @Preview(name = "CountryListError", showBackground = true)
    @Composable
    fun CountryListErrorScreenshots() {
        CountryListErrorPreview()
    }

    @PreviewTest
    @Preview(name = "CountryListLoaded", showBackground = true)
    @Composable
    fun CountryListLoadedScreenshots() {
        CountryListLoadedPreview()
    }

    @PreviewTest
    @Preview(name = "CountryListLoadedEmpty", showBackground = true)
    @Composable
    fun CountryListLoadedEmptyScreenshots() {
        CountryListLoadedEmptyPreview()
    }
}