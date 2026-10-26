package uk.gov.govuk.travelalerts.ui.editcountries

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.android.tools.screenshot.PreviewTest

internal class EditCountriesScreenScreenshotTests {
    @PreviewTest
    @PreviewLightDark
    @Composable
    fun LoadingScreenScreenshots() {
        EditCountriesLoadingPreview()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun EditCountriesErrorScreenshots() {
        EditCountriesErrorPreview()
    }

    @PreviewTest
    @PreviewLightDark
    @Composable
    fun EditCountriesLoadedScreenshots() {
        EditCountriesLoadedPreview()
    }
}