package uk.gov.govuk.travelalerts.ui.editcountries

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest

internal class EditCountriesScreenScreenshotTests {
    @PreviewTest
    @Preview(name = "LoadingScreen", showBackground = true)
    @Composable
    fun LoadingScreenScreenshots() {
        EditCountriesLoadingPreview()
    }

    @PreviewTest
    @Preview(name = "EditCountriesError", showBackground = true)
    @Composable
    fun EditCountriesErrorScreenshots() {
        EditCountriesErrorPreview()
    }

    @PreviewTest
    @Preview(name = "EditCountriesLoaded", showBackground = true)
    @Composable
    fun EditCountriesLoadedScreenshots() {
        EditCountriesLoadedPreview()
    }
}